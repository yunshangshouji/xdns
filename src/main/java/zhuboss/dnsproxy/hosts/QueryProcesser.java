package zhuboss.dnsproxy.hosts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xbill.DNS.Message;
import org.xbill.DNS.Record;
import org.xbill.DNS.Section;
import org.xbill.DNS.Type;

import zhuboss.dnsproxy.config.DomainPatternsContainer;

public class QueryProcesser {
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	AnswerPatternProvider answerProvider = new AnswerPatternProvider();
	
	 public void handle(DomainPatternsContainer domainPatternsContainer,Message request,HandleResponse response) {
	        Record question = request.getQuestion();
	        String query = question.getName().toString();
	        int type = question.getType();
	        // some client will query with any
	        if (type == Type.ANY) {
	            type = Type.A;
	        }

            String answer = answerProvider.getAnswer(domainPatternsContainer,query, type);
            if (answer != null) {
                try {
                    Record record = new RecordBuilder()
                            .dclass(question.getDClass())
                            .name(question.getName()).answer(answer).type(type)
                            .toRecord();
                    response.getMessage().addRecord(record, Section.ANSWER);
                    response.setHasRecord(true);
                    return;
                } catch (Throwable e) {
                    logger.warn("handling exception " + e);e.printStackTrace();
                }
            }
        
	        
	    }
}
