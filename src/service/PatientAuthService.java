package service;

import config.AuthSecurityConfig;
import dao.AuditDAO;
import dao.PatientAuthDAO;
import model.PatientIdentity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import security.PasswordUtil;
import security.PatientSession;
import security.SessionManager;
import util.ValidationUtil;
import java.sql.SQLException;
import java.util.Arrays;

/** Isolated patient credential flow. Patient credentials can never create staff sessions. */
public final class PatientAuthService {
    private static final Logger LOG=LoggerFactory.getLogger(PatientAuthService.class);
    private final PatientAuthDAO dao; private final PatientSession sessions; private final AuditDAO audit;
    public PatientAuthService(){this(new PatientAuthDAO(),PatientSession.INSTANCE,new AuditDAO());}
    public PatientAuthService(PatientAuthDAO dao,PatientSession sessions,AuditDAO audit){this.dao=dao;this.sessions=sessions;this.audit=audit;}
    public PatientIdentity authenticate(String id,char[] password)throws AuthException{
        char[] supplied=password==null?new char[0]:password;
        try{
            if(sessions.isLoggedIn() || SessionManager.INSTANCE.isLoggedIn())throw new AuthException("Sign out before starting another session.");
            if(!ValidationUtil.isValidLogin(id,supplied))throw new AuthException("Enter your email or patient username and password.");
            var found=dao.findByIdentifier(id.trim()); if(found.isEmpty()){audit.recordEvent(null,"PATIENT_LOGIN_FAILURE");throw denied();}
            var account=found.get();
            if("LOCKED".equals(account.status())&&dao.restoreExpiredLock(account.patientId()))account=dao.findByIdentifier(id.trim()).orElse(account);
            if(!PasswordUtil.verifyPassword(supplied,account.hash())){
                boolean locked="ACTIVE".equals(account.status())&&dao.recordFailure(account.patientId(),AuthSecurityConfig.MAX_FAILED_ATTEMPTS,AuthSecurityConfig.LOCKOUT_MINUTES);
                audit.recordEvent(null,locked?"PATIENT_ACCOUNT_LOCKED":"PATIENT_LOGIN_FAILURE");throw denied();
            }
            if(!"ACTIVE".equals(account.status())){audit.recordEvent(null,"PATIENT_LOGIN_FAILURE");throw denied();}
            if(!dao.recordSuccess(account.patientId()))throw denied();
            audit.recordEvent(null,"PATIENT_LOGIN_SUCCESS");
            return sessions.create(account.patientId(),account.name());
        }catch(SQLException ex){LOG.error("Patient authentication operation failed.",ex);throw new AuthException("Unable to connect to the hospital database.");}
        finally{Arrays.fill(supplied,'\0');}
    }
    private AuthException denied(){return new AuthException("Your username or password is incorrect.");}
    public void logout(){sessions.logout();}
    public static class AuthException extends Exception{public AuthException(String message){super(message);}}
}
