"use strict";

var _helpers = require("./helpers");

var _api = require("./api");

var _constants = _interopRequireDefault(require("./constants"));

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

var dispatch = function dispatch(intentRequest, callback) {
  // var request = JSON.parse(intentRequest);
  console.log("request received for userId=${intentRequest.userId}, intentName=${intentRequest.currentIntent.intentName}");
  var sessionAttributes = intentRequest.sessionAttributes;
  var intentName = intentRequest.currentIntent.name;
  console.log("request" + intentRequest);
  console.log("Intent: " + intentName);
  console.log("Session: " + sessionAttributes);
  console.log("Constant: " + _constants.default.USERNAME);

  switch (intentName) {
    case _constants.default.NEWDOCAPPOINTMENT:
      callback((0, _helpers.close)(sessionAttributes, "Fulfilled", {
        "contentType": "PlainText",
        "content": "Affe"
      }));
      break;

    case _constants.default.USERNAME:
      console.log("Test");
      callback((0, _helpers.close)(sessionAttributes, "Fulfilled", {
        "contentType": "PlainText",
        "content": (0, _api.getUserName)()
      }));
      break;

    default:
      callback((0, _helpers.close)(sessionAttributes, "Fulfilled", {
        "contentType": "PlainText",
        "content": "Giraffe"
      }));
  }

  callback((0, _helpers.close)(sessionAttributes, "Fulfilled", {
    "contentType": "PlainText",
    "content": "Affe"
  }));
};

exports.handler = function (event, context, callback) {
  console.log("event: " + event);
  console.log("context: " + context);

  try {
    dispatch(event, function (response) {
      callback(null, response);
    });
  } catch (err) {
    callback(err);
  }
};