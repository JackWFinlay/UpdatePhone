# UpdatePhone
A Scriptella ETL script to update phone numbers.

This script makes use of a webservice to update phone numbers in a database. 
The numbers are extracted from a CSV file, user IDs manipulated, and then posted to the webservice via SOAP.

#### Files

*UpdatePhone.Properties* - A properties file to hold information about the server and usernames passwords. Blank here for security reasons.

*UpdatePhone.groovy* - A Groovy script to execute the update.

*UpdatePhone.etl.xml* - A Scriptella script that does the same as the Groovy script, but for the Scriptella platform. 

*logback.xml* - Sets the properties for Logback/SLF4J logging. Currently set to print to the console and write out to a .log file. In future, will also send to a remote service.
