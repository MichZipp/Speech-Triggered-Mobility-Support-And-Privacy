 // Close dialog with the customer, reporting fulfillmentState of Failed or Fulfilled ("Thanks, your pizza will arrive in 20 minutes")
 function close(sessionAttributes, fulfillmentState, message) {
    return {
        sessionAttributes,
        dialogAction: {
            type: "Close",
            fulfillmentState,
            message,
        },
    };
}

// --------------- Events ----------------------- 
function dispatch(intentRequest, callback) {
    console.log("request received for userId=${intentRequest.userId}, intentName=${intentRequest.currentIntent.intentName}");
    const sessionAttributes = intentRequest.sessionAttributes;
    const intentName = intentRequest.currentIntent;
    
    console.log("Intent: " + intentName);
    console.log("Session: " + sessionAttributes);
    
    callback(close(sessionAttributes, "Fulfilled", {"contentType": "PlainText", "content": "Affe"}));
}

// --------------- Main handler ----------------------
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