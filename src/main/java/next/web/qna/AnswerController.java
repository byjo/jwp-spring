package next.web.qna;

import java.util.List;

import javax.annotation.Resource;
import javax.validation.Valid;

import next.model.qna.Answer;
import next.service.qna.QnaService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import core.web.Result;

@Controller
@Scope("prototype")
@RequestMapping("/api/questions/{questionId}/answers")
public class AnswerController {
	private static final Logger logger = LoggerFactory.getLogger(AnswerController.class);
	
	@Resource(name = "messageSourceAccessor")
	private MessageSourceAccessor msa;
	
	@Resource(name = "qnaService")
	private QnaService qnaService;
	
	@RequestMapping(value="", method=RequestMethod.POST)
	public @ResponseBody Result save(@PathVariable long questionId, @Valid Answer answer, BindingResult bindingResult) {
		logger.debug("Answer : {}", answer);
		if (bindingResult.hasFieldErrors()) {
			List<FieldError> errors = bindingResult.getFieldErrors();
			for (FieldError error : errors) {
				return Result.fail(getMessage(error));
			}
		}
		
		qnaService.addAnswer(questionId, answer);
		
		return Result.ok();
	}
	
	@RequestMapping(value="/{answerId}", method=RequestMethod.DELETE)
	public @ResponseBody Result delete(@PathVariable long questionId, @PathVariable long answerId) {
		logger.debug("questionId : {}, answerId : {}", questionId, answerId);
		qnaService.deleteAnswer(questionId, answerId);
		return Result.ok();
	}
	
	private String getMessage(FieldError error) {
		String key = error.getCode() + ".answer." + error.getField();
		return msa.getMessage(key);
	}
}
