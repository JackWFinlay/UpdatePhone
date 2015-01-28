import wslite.soap.*
import au.com.bytecode.opencsv.CSVReader
import java.util.logging.Logger

Logger log = Logger.getLogger("")

Properties properties = new Properties()
File propsFile = new File('UpdatePhone.properties')
properties.load(propsFile.newDataInputStream())
log.info("Loaded properties file")

String hostname = properties.getProperty('hostname')
String username = properties.getProperty('username')
String password = properties.getProperty('password')
String csvFile  = properties.getProperty('CSVFile')

log.info("Populated fields from properties file")
// Assign variables from  properties file

SOAPClient wsClient = new SOAPClient(hostname)

CSVReader reader = new CSVReader(new FileReader(csvFile))
String [] contactDetailElem

while ((contactDetailElem = reader.readNext()) != null) {
    // Until no more elements

    String rawID = contactDetailElem[4]

    if (rawID.contains("!")){

        String[] s = rawID.split("!")
        String personID = s[1]
        log.info("Extracted UPI: \"" + personID + "\" from entry" + rawID)

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

        log.info( (String)eprUserDataResponse )

        } else {

          log.info("Skipping entry \"" + rawID + "\", No UPI or bad format")

        }

}
