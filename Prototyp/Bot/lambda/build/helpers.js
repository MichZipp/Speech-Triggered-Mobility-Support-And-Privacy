"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.delegate = exports.elicitSlot = exports.close = void 0;

var close = function close(sessionAttributes, fulfillmentState, message) {
  return {
    sessionAttributes: sessionAttributes,
    dialogAction: {
      type: "Close",
      fulfillmentState: fulfillmentState,
      message: message
    }
  };
};

exports.close = close;

var elicitSlot = function elicitSlot(sessionAttributes, intentName, slots, slotToElicit, message) {
  return {
    sessionAttributes: sessionAttributes,
    dialogAction: {
      type: 'ElicitSlot',
      intentName: intentName,
      slots: slots,
      slotToElicit: slotToElicit,
      message: message
    }
  };
};

exports.elicitSlot = elicitSlot;

var delegate = function delegate(sessionAttributes, slots) {
  return {
    sessionAttributes: sessionAttributes,
    dialogAction: {
      type: 'Delegate',
      slots: slots
    }
  };
};

exports.delegate = delegate;