import { close, elictSlot, delegate } from "./helpers";
import { getUserName, getUserLocation, getUserProfileName, getDocs } from "./responseBuilder";
import { getUserSettings } from "./api";
import I from "./constants";

const dispatch = (intentRequest, callback) => {
    // var request = JSON.parse(intentRequest);
    console.log(intentRequest);
    console.log("request received for userId=${intentRequest.userId}, intentName=${intentRequest.currentIntent.intentName}");
    const sessionAttributes = intentRequest.sessionAttributes;
    console.log(sessionAttributes);
    const access_token = sessionAttributes.accessToken;
    const user_id = sessionAttributes.userId;
    const intentName = intentRequest.currentIntent.name;

    var responseMessage = "This is a default message";

    switch(intentName){
        case I.NEWDOCAPPOINTMENT:
            callback(close(sessionAttributes, "Affe" ));
            break;
        case I.USERNAME:
            getUserName(access_token, user_id)
            .then( response => {
                callback(close(sessionAttributes, response));
            })
            .catch( error => {
                callback(close(sessionAttributes, error));            
            });           
            break;
        case I.USERLOCATION:
            getUserLocation(access_token, user_id)
            .then( response => {
                callback(close(sessionAttributes, response));
            })
            .catch( error => {
                callback(close(sessionAttributes, error));            
            }); 
            break;
        case I.DOCS:
            getDocs(access_token, user_id)
            .then( response => {
                callback(close(sessionAttributes, response));
            })
            .catch( error => {
                callback(close(sessionAttributes, error));            
            }); 
            break;
        default:
            callback(close(sessionAttributes, "Giraffe"));
    }
}

exports.handler = (event, context, callback) => {
    console.log("event: " + event);
    console.log("context: " + context);
    try {
        dispatch(event,
            (response) => {
                callback(null, response);
            });
    } catch (err) {
        callback(err);
    }
};