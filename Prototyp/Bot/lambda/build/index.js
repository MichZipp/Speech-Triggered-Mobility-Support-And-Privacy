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

  switch (intentName) {
    case _constants.default.NEWDOCAPPOINTMENT:
      callback((0, _helpers.close)(sessionAttributes, "Affe"));
      break;

    case _constants.default.USERNAME:
      callback((0, _helpers.close)(sessionAttributes, "Are you kiddinng my, your name is: " + (0, _api.getUserName)()));
      break;

    case _constants.default.USERLOCATION:
      callback((0, _helpers.close)(sessionAttributes, "Actually you are in " + (0, _api.getUserLocation)()));
      break;

    case _constants.default.USERPROFILE:
      callback((0, _helpers.close)(sessionAttributes, "You Profile " + (0, _api.getUserProfileName)() + "is currently activated!"));
      break;

    case _constants.default.DOCS:
      callback((0, _helpers.close)(sessionAttributes, (0, _api.getDocs)()));
      break;

    default:
      callback((0, _helpers.close)(sessionAttributes, "Giraffe"));
  }

  callback((0, _helpers.close)(sessionAttributes, "Affe"));
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