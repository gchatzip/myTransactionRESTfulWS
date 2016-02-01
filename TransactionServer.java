package mytransaction;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.Produces;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.Consumes;
import javax.ws.rs.PathParam;
//import the following library to handle JSON
import org.json.simple.JSONObject;





/**
 *
 * @author George
 * the URI will be localhost:8080/CONTEXTPATH/APPLICATIONPATH/transactionservice/{operation} ... 
 */
@Path("/transactionservice")
@ApplicationPath("/resources")
public class TransactionServer extends Application {
    
    //contains all transactions the key is the transaction id
    public static Map<Long,TransactionObject> transactionmap;
    //map the type(which is the key) to all the ids of the same type 
    public static Map<String,ArrayList<Long>> typeList;
    //map the parent_id(which is the key) to all the childern transaction id
    public static Map<Long,ArrayList<Long>> sumList;
    
    
    //initialize the data structures
    public TransactionServer() {
          transactionmap = new HashMap<>();
          typeList = new HashMap<>();
          sumList = new HashMap<>();
    }

    //implement the requests as the API specifies
    
    
    // GET /transactionservice/transaction/$transaction_id
    @GET
    @Path("/transaction/{transaction_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public TransactionObject returnTransaction(@PathParam("transaction_id") long tid){
        //return the transaction object
        if (transactionmap.containsKey(tid)){
           TransactionObject output = transactionmap.get(tid);
           return output;
        }
        else {
            return null;
        }
        
    }
    // GET /transactionservice/types/$type
    @GET
    @Path("/types/{type}")
    @Produces(MediaType.APPLICATION_JSON)
    public StringBuffer returnTypes(@PathParam("type") String type){

        if (typeList.containsKey(type)){
             ArrayList<Long> types_tid = typeList.get(type);
             StringBuffer out_buffer =new StringBuffer();
             out_buffer.append("[");
             for (Long tid : types_tid) {
                 out_buffer.append(tid+",");
             }
             out_buffer.append("]");
             return out_buffer;
        }
        else { // type does not exist
            StringBuffer out_buffer = new StringBuffer();
            out_buffer.append("{type:null}");
            return out_buffer;
        } 
    }
    // GET /transactionservice/sum/$transaction_id
    @GET
    @Path("/sum/{transaction_id}")
    @Produces(MediaType.APPLICATION_JSON)
    public String returnSum(@PathParam("transaction_id") long tid){

        if (transactionmap.containsKey(tid)){

            Double linked_sum = 0.0;
            Double amt_to_add = 0.0;
            ArrayList<Long> child_tid_list = sumList.get(tid);
            for (Long iterate_id : child_tid_list){
                TransactionObject get_amt = transactionmap.get(tid);
                amt_to_add = get_amt.getAmount();
                linked_sum =  amt_to_add;
            }
            return "{ \"sum\","+linked_sum+"\"}\"";
        }
        else { //bad value provided
            return "{\"sum\":null}";
        }

    }

    // PUT /transactionservice/transaction/$transaction_id
    @PUT
    @Path("/transaction/{transaction_id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public String insertTransaction(@PathParam("transaction_id") long tid, InputStream incomingData){
  
        StringBuilder inBuilder = new StringBuilder();
	try {
            BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
            String line = null;
            while ((line = in.readLine()) != null) {
                inBuilder.append(line);
            }
            /* parse JSON and initialize the desired fields */
            if (inBuilder.length()!=0){ 
                JSONObject nodeRoot  = new JSONObject(inBuilder); 
                Double amount = nodeRoot.getDouble("amount");
                String type   = nodeRoot.getString("type");
                String pid    = nodeRoot.getString("parent_id");
                if (pid == null) { // no parent id provided
                    transactionmap.put(tid, new TransactionObject(tid, amount,type ));
                    //populate the type list
                    if (typeList.containsKey(type)){
                        ArrayList<Long> lid = typeList.get(type);
                        lid.add(tid);
                    }
                    else {
                        ArrayList<Long> lid = new ArrayList<>();
                        lid.add(tid);
                        typeList.put(type,lid);
                    }
                }
                else { // parentid provided
                   Long parent_id = Long.parseLong(pid, 10);
                   transactionmap.put(tid, new TransactionObject(tid, amount,type,parent_id ));
                   //populate type list
                   if (typeList.containsKey(type)){
                        ArrayList<Long> lid = typeList.get(type);
                        lid.add(tid);
                    }
                    else {
                        ArrayList<Long> lid = new ArrayList<>();
                        lid.add(tid);
                        typeList.put(type,lid);
                    }
                   //populate sum list
                   if (sumList.containsKey(parent_id)){ //append new transaction id to the list
                       
                       ArrayList<Long> existing = sumList.get(parent_id);
                       existing.add(tid);
                       sumList.replace(parent_id, existing);
                   }
                   else {  // initialize the list
                       ArrayList<Long> newlist = new ArrayList<>();
                       newlist.add(tid);
                       sumList.put(parent_id, newlist);
                   }
                    
                }
            }
            else {
                return "{parse error}";
            }
            
            
	} catch (Exception e) {
		System.out.println("Error Parsing: - ");
                System.out.println(e.getStackTrace());
                return "{parse error}";
		}
	

        return "{\"status\":\"ok\"}";

    }

      public static void main(String args[]){
        //fill the server with dummy transactions
        TransactionObject t1 = new TransactionObject(0,15000,"cars");
        TransactionObject t2 = new TransactionObject(1,10000,"cars",1);
        TransactionObject t3 = new TransactionObject(2,1100,"clothes");
        TransactionObject t4 = new TransactionObject(3,1300,"clothes",2);
        TransactionObject t5 = new TransactionObject(4,150,"grocery");

        transactionmap.put(t1.getTransactionId(), t1);
        transactionmap.put(t2.getTransactionId(), t2);
        transactionmap.put(t3.getTransactionId(), t3);
        transactionmap.put(t4.getTransactionId(), t4);
        transactionmap.put(t5.getTransactionId(), t5);
        
        


    }


}
