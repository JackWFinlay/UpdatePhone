# UpdatePhone
A script to update phone numbers

This script makes use of a webservice to update phone numbers in a database. 
The numbers are extracted from a CSV file, user IDs manipulated, and then posted to the webservice via SOAP.

*UpdatePhone.Properties* - A properties file to hold information about the server and usernames passwords. Blank here for security reasons.

*UpdatePhone.groovy* - A Groovy script to execute the update.

*UpdatePhone.etl.xml* - A Scriptella script that does the same as the Groovy script, but for the Scriptella platform. 
