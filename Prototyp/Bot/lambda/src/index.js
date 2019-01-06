import { close, elictSlot, delegate } from "./helpers";
import { getUserName, getUserLocation, getUserProfileName, getDocs } from "./api";
import I from "./constants";

const dispatch = (intentRequest, callback) => {
    // var request = JSON.parse(intentRequest);
    console.log("request received for userId=${intentRequest.userId}, intentName=${intentRequest.currentIntent.intentName}");
    const sessionAttributes = intentRequest.sessionAttributes;
    const intentName = intentRequest.currentIntent.name;

    switch(intentName){
        case I.NEWDOCAPPOINTMENT:
            callback(close(sessionAttributes, "Affe" ));
            break;
        case I.USERNAME:
            callback(close(sessionAttributes, "Are you kiddinng my, your name is: " + getUserName()));
            break;
        case I.USERLOCATION:
            callback(close(sessionAttributes, "Actually you are in " + getUserLocation()));
            break;
        case I.USERPROFILE:
            callback(close(sessionAttributes, "You Profile " + getUserProfileName() + "is currently activated!"));
            break;
        case I.DOCS:
            callback(close(sessionAttributes, getDocs()));
            break;
        default:
            callback(close(sessionAttributes, "Giraffe"));
    }
    
    callback(close(sessionAttributes, "Affe"));
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