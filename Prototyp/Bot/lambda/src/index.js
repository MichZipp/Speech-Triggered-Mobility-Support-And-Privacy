import { close, elictSlot, delegate } from "./helpers";
import { getUserName, getUserLocation, getUserProfileName, getDocs } from "./responseBuilder";
import { getUserSettings } from "./api";
import I from "./constants";

const dispatch = (intentRequest, callback) => {
    // var request = JSON.parse(intentRequest);
    console.log("request received for userId=${intentRequest.userId}, intentName=${intentRequest.currentIntent.intentName}");
    const sessionAttributes = intentRequest.sessionAttributes;
    console.log(sessionAttributes);
    const access_token = sessionAttributes.accessToken;
    const user_id = sessionAttributes.userId;
    const setting = getUserSettings(access_token, user_id);
    const profile_id = settings.activeprofile;
    const intentName = intentRequest.currentIntent.name;

    switch(intentName){
        case I.NEWDOCAPPOINTMENT:
            callback(close(sessionAttributes, "Affe" ));
            break;
        case I.USERNAME:
            callback(close(sessionAttributes, getUserName(access_token, profile_id)));
            break;
        case I.USERLOCATION:
            callback(close(sessionAttributes, );
            break;
        case I.USERPROFILE:
            callback(close(sessionAttributes, getUserProfileName(access_token, profile_id)));
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