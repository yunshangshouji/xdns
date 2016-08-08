package zhuboss.framework.server.jetty.sessionhandler;

import java.io.IOException;
import java.net.UnknownHostException;

import org.eclipse.jetty.nosql.NoSqlSession;
import org.eclipse.jetty.nosql.mongodb.MongoSessionManager;
import org.eclipse.jetty.server.session.AbstractSession;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

import com.mongodb.MongoException;

/** 
 * @author 作者 zhuzhengquan: 
 * @version 创建时间：2015年11月24日 上午8:42:10 
 * @description 这个类是要去除MongoSessionManage实现的session在节点缓存的目的。因为缓存后，其它节点对session修改将不能感知
 */
public class MyMongoSessionManager extends MongoSessionManager {

	private final static Logger __log = Log.getLogger("org.eclipse.jetty.server.session");
	
	public MyMongoSessionManager() throws UnknownHostException, MongoException {
		super();
	}

	@Override
	public AbstractSession getSession(String idInCluster) {

        NoSqlSession session = loadSession(idInCluster);
        __log.debug("getSession {} ", session );
        
        if (session!=null)
        {
            //check if the session we just loaded has actually expired, maybe while we weren't running
            if (getMaxInactiveInterval() > 0 && session.getAccessed() > 0 && ((getMaxInactiveInterval()*1000L)+session.getAccessed()) < System.currentTimeMillis())
            {
                __log.debug("session expired ", idInCluster);
                expire(idInCluster);
                session = null;
            }
        }
        else
            __log.debug("session does not exist {}", idInCluster);
    
        return session;
    
	}

	@Override
	protected Object decodeValue(Object valueToDecode)  {
		try{
			return super.decodeValue(valueToDecode);
		}catch(ClassNotFoundException e1){
			//ClassNotFoundException session中可能不同的应用存进入不同的无法序列化的对象，这时候容错，返回null，而不要导致整个loadSession失败！
			
			return null; 
		}
		catch(IOException e2 ){
			__log.warn(e2);
			
			return null; 
		}
	}

	
	
}
