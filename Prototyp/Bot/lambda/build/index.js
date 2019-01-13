"use strict";

var _helpers = require("./helpers");

var _responseBuilder = require("./responseBuilder");

var _api = require("./api");

var _constants = _interopRequireDefault(require("./constants"));

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

var dispatch = function dispatch(intentRequest, callback) {
  // var request = JSON.parse(intentRequest);
  console.log(intentRequest);
  console.log("request received for userId=${intentRequest.userId}, intentName=${intentRequest.currentIntent.intentName}");
  var sessionAttributes = intentRequest.sessionAttributes;
  console.log(JSON.stringify(sessionAttributes));
  var access_token = sessionAttributes.accessToken;
  var user_id = sessionAttributes.userId;
  var intentName = intentRequest.currentIntent.name;
  var responseMessage = "This is a default message";

  switch (intentName) {
    case _constants.default.NEWDOCAPPOINTMENT:
      (0, _responseBuilder.getNewDocAppointmet)(access_token, user_id, callback, sessionAttributes);
      break;

    case _constants.default.USERNAME:
      (0, _responseBuilder.getUserName)(access_token, user_id).then(function (response) {
        callback((0, _helpers.close)(sessionAttributes, response));
      }).catch(function (error) {
        callback((0, _helpers.close)(sessionAttributes, error));
      });
      break;

    case _constants.default.USERLOCATION:
      (0, _responseBuilder.getUserLocation)(access_token, user_id).then(function (response) {
        callback((0, _helpers.close)(sessionAttributes, response));
      }).catch(function (error) {
        callback((0, _helpers.close)(sessionAttributes, error));
      });
      break;

    case _constants.default.DOCS:
      (0, _responseBuilder.getDocs)(access_token, user_id).then(function (response) {
        callback((0, _helpers.close)(sessionAttributes, response));
      }).catch(function (error) {
        callback((0, _helpers.close)(sessionAttributes, error));
      });
      break;

    default:
      callback((0, _helpers.close)(sessionAttributes, "Giraffe"));
  }
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