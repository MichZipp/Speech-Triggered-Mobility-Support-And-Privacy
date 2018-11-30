import { close, elictSlot, delegate } from "./helpers";

const dispatch = (intentRequest, callback) => {
    console.log("request received for userId=${intentRequest.userId}, intentName=${intentRequest.currentIntent.intentName}");
    const sessionAttributes = intentRequest.sessionAttributes;
    const intentName = intentRequest.currentIntent;
    
    console.log("Intent: " + intentName);
    console.log("Session: " + sessionAttributes);
    
    callback(close(sessionAttributes, "Fulfilled", {"contentType": "PlainText", "content": "Affe"}));
}

exports.handler = (event, context, callback) => {
    try {
        dispatch(event,
            (response) => {
                callback(null, response);
            });
    } catch (err) {
        callback(err);
    }
};