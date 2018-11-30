let { close, elictSlot, delegate } =  require("./helpers");
let I = require("./constants");

const dispatch = (intentRequest, callback) => {
    console.log("request received for userId=${intentRequest.userId}, intentName=${intentRequest.currentIntent.intentName}");
    const sessionAttributes = intentRequest.sessionAttributes;
    const intentName = intentRequest.currentIntent;

    console.log("Intent: " + intentName);
    console.log("Session: " + sessionAttributes);
    
    switch(intentName){
        case I.NEWDOCAPPOINTMENT:
            callback(close(sessionAttributes, "Fulfilled", {"contentType": "PlainText", "content": "Affe"}));
            break;
        default:
            callback(close(sessionAttributes, "Fulfilled", {"contentType": "PlainText", "content": "Giraffe"}));
    }
    
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