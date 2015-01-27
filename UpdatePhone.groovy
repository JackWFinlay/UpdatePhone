import wslite.soap.*
import au.com.bytecode.opencsv.CSVReader
import java.util.logging.Logger

Logger log = Logger.getLogger("")

SOAPClient wsClient
String hostname
String username
String password

String csvFile = "numbers.csv" // Location of file

Properties properties = new Properties()
File propsFile = new File('UpdatePhone.properties')
properties.load(propsFile.newDataInputStream())
log.info("Loaded properties file")


hostname = properties.getProperty('hostname')
username = properties.getProperty('username')
password = properties.getProperty('password')
log.info("Populated fields from properties file")
// Assign variables from  properties file

wsClient = new SOAPClient(hostname)

CSVReader reader = new CSVReader(new FileReader(csvFile))
String [] contactDetailElem

while ((contactDetailElem = reader.readNext()) != null) {
    // Until no more elements

    String rawID = contactDetailElem[4]
    String parsedID = ""

    if (rawID.contains("!")){

        String[] s = rawID.split("!")
        parsedID = s[1]
        log.info("Extracted UPI: \"" + parsedID + "\" from entry" + rawID)

    } else {

        log.info("Skipping entry \"" + rawID + "\", No UPI or bad format")
        continue //Skips over this entry as it is invalid.

    }

    String personID = parsedID
    String contactDetail = contactDetailElem[3]
    String phoneType = "Campus"
    // Obtain values
    log.info("Updating element " + personID + ", " + contactDetail + ", " + phoneType)


    def eprUserDataResponse = wsClient.send(connectTimeout: 5000, readTimeout: 15000) {
        envelopeAttributes "xmlns:auc": "http://www.auckland.ac.nz"
        version SOAPVersion.V1_1

        header {
            'wsse:Security'('soap-env:mustUnderstand': "1", 'xmlns:wsse': 'http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd', 'xmlns:wsu': 'http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd') {
                'wsse:UsernameToken'('wsu:Id': "UsernameToken-4") {
                    'wsse:Username'(username)
                    'wsse:Password'('Type': 'http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordText', password)
                    'wsse:Nonce'('EncodingType': 'http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-soap-message-security-1.0#Base64Binary', new String(password.bytes.encodeBase64().toString()))
                    'wsu:Created'(new Date().format("yyyy-MM-dd'T'HH:mm:ss SSS"))
                }
            }
        }

        body {
            'auc:updatePhone'{
                'auc:phoneType'(phoneType)
                'auc:ArrayOfContactDetailPair' {
                    'auc:ContactDetailElem' {
                        'auc:personID'(personID)
                        'auc:contactDetail'(contactDetail)
                    }
                }

            }
        }
    }

    log.info( (String)eprUserDataResponse)

}
