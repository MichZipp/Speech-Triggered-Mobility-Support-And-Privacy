"use strict";

var _require = require("./helpers"),
    close = _require.close,
    elictSlot = _require.elictSlot,
    delegate = _require.delegate;

var I = require("./constants");

var dispatch = function dispatch(intentRequest, callback) {
  console.log("request received for userId=${intentRequest.userId}, intentName=${intentRequest.currentIntent.intentName}");
  var sessionAttributes = intentRequest.sessionAttributes;
  var intentName = intentRequest.currentIntent;
  console.log("Intent: " + intentName);
  console.log("Session: " + sessionAttributes);

  switch (intentName) {
    case I.NEWDOCAPPOINTMENT:
      callback(close(sessionAttributes, "Fulfilled", {
        "contentType": "PlainText",
        "content": "Affe"
      }));
      break;

    default:
      callback(close(sessionAttributes, "Fulfilled", {
        "contentType": "PlainText",
        "content": "Giraffe"
      }));
  }

  callback(close(sessionAttributes, "Fulfilled", {
    "contentType": "PlainText",
    "content": "Affe"
  }));
};

exports.handler = function (event, context, callback) {
  try {
    dispatch(event, function (response) {
      callback(null, response);
    });
  } catch (err) {
    callback(err);
  }
};