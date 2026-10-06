package dao;

import database.DatabaseConnection;
import java.sql.*;
import java.util.Optional;

/** Persistence boundary for credentials attached to existing patient records. */
public final class PatientAuthDAO {
    public record Credential(long patientId, String name, String hash, String status, int attempts, Timestamp lockedUntil) { }
    public Optional<Credential> findByIdentifier(String id) throws SQLException {
        String sql="SELECT c.patient_id,p.name,c.password_hash,c.status,c.failed_login_attempts,c.locked_until FROM patient_credentials c JOIN patients p USING(patient_id) WHERE lower(c.username)=lower(?) OR lower(c.email)=lower(?) ORDER BY c.patient_credential_id LIMIT 2";
        try(Connection c=DatabaseConnection.getConnection();PreparedStatement s=c.prepareStatement(sql)){s.setString(1,id);s.setString(2,id);try(ResultSet r=s.executeQuery()){if(!r.next())return Optional.empty();Credential result=map(r);if(r.next())return Optional.empty();return Optional.of(result);}}
    }
    private Credential map(ResultSet r)throws SQLException{return new Credential(r.getLong(1),r.getString(2),r.getString(3),r.getString(4),r.getInt(5),r.getTimestamp(6));}
    public boolean restoreExpiredLock(long id)throws SQLException{return update("UPDATE patient_credentials SET status='ACTIVE',failed_login_attempts=0,locked_until=NULL,updated_at=CURRENT_TIMESTAMP WHERE patient_id=? AND status='LOCKED' AND locked_until IS NOT NULL AND locked_until<=CURRENT_TIMESTAMP",id);}
    public boolean recordFailure(long id,int max,int minutes)throws SQLException{String q="UPDATE patient_credentials SET failed_login_attempts=failed_login_attempts+1,status=CASE WHEN failed_login_attempts+1>=? THEN 'LOCKED' ELSE status END,locked_until=CASE WHEN failed_login_attempts+1>=? THEN CURRENT_TIMESTAMP+(?*INTERVAL '1 minute') ELSE locked_until END,updated_at=CURRENT_TIMESTAMP WHERE patient_id=? AND status='ACTIVE'";try(Connection c=DatabaseConnection.getConnection();PreparedStatement s=c.prepareStatement(q)){s.setInt(1,max);s.setInt(2,max);s.setInt(3,minutes);s.setLong(4,id);return s.executeUpdate()==1;}}
    public boolean recordSuccess(long id)throws SQLException{return update("UPDATE patient_credentials SET last_login=CURRENT_TIMESTAMP,failed_login_attempts=0,locked_until=NULL,updated_at=CURRENT_TIMESTAMP WHERE patient_id=? AND status='ACTIVE'",id);}
    private boolean update(String q,long id)throws SQLException{try(Connection c=DatabaseConnection.getConnection();PreparedStatement s=c.prepareStatement(q)){s.setLong(1,id);return s.executeUpdate()==1;}}
}
