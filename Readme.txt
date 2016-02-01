*COMMENTS about the implementation and a brief discussion about the time complexity*


Adding the two files that i have used to in order to implement the desired API.
*TransactionObject.java which models the transaction as an object.
*TransactionServer.java which handles the requests to the web service and outputs the results whilst also doing
some dummy initialization in order to add some initial values.

I have tested the functionality using Glassfish server 4.0.1 and the RESTConsole Google chrome extension to issue GET/PUT requests to the WS.
*the URI that the API listens to is : localhost:8080/CONTEXTPATH/APPLICATIONPATH/transactionservice/{operation} 

Regarding the asymptotic behaviour : 
*In order to store each transaction i am using a HashMap which maps the KEY(transaction_id) to the  transaction object with that key.
HashMap implementation will allow us to fetch values in O(1) additionally inserting a transaction in the map costs O(1) as well.

*In order to be able to transitively link the transaction with a specified parent_id i am using 
a HashMap which uses the parent id as a key and the value is an ArrayList of transaction id which are linked the key.

The above will allow us to need O(1) in order to find the parent_id bucket and fetch the ArrayList object with all the linked transaction_id,
then we will use each transaction_id in order to fetch the amount of transaction of each individual transaction from the transaction storage map.
This will cost us O(n^2) for the GET /SUM/ request in the worst case.

*Lastly in order to output types i am using a HashMap which uses the type as key and then maps the key with an arraylist containing all 
the transaction_id with this particular type.
The GET /type/ operation will cost O(n) in the worst case. 