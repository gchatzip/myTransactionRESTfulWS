package mytransaction;
import javax.xml.bind.annotation.XmlRootElement;
/**
 *
 * @author George
 * 
 */
@XmlRootElement
public class TransactionObject {
    private long transaction_id;
    private double amount;
    private String type;
    private long parent_id;
    
    public TransactionObject() {}
    //initialize parent id to -1 when there is no pid provided
    public TransactionObject(long tid,double amt,String type){
        this.transaction_id=tid;
        this.amount=amt;
        this.type=type;
        this.parent_id=-1;
    }
    public TransactionObject(long tid,double amt,String type,long pid){
        this.transaction_id=tid;
        this.amount=amt;
        this.type=type;
        this.parent_id=pid;
    }
    public long getTransactionId(){
        return this.transaction_id;
    }
    public void setTransactionId(long tid){
        this.transaction_id=tid;
    }
    public double getAmount(){
        return this.amount;
    }
    public void setAmount(double amt){
        this.amount=amt;
    }
    public String getType(){
        return this.type;
    }
    public void setType(String type){
        this.type=type;
    }
    public long getParentId(){
        return this.parent_id;
    }
    public void setParentId(long pid){
        this.parent_id=pid;
    }
    
    @Override
    public String toString(){
        if (this.parent_id!=-1){
            return "{ \"amount\":"+this.amount+",\"type\":"+this.type+",\"parent_id\":"+this.parent_id+"}";
        }
        else {
            return "{ \"amount\":"+this.amount+",\"type\":"+this.type+"]";
        }
    }
}
