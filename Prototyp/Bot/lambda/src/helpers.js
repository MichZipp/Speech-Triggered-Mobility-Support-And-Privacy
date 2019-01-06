 export const close = (sessionAttributes, msg) => {
    return {
        sessionAttributes,
        dialogAction: {
            type: "Close",
            fulfillmentState: "Fulfilled",
            message: {"contentType": "PlainText", "content": msg}
        },
    };
}

export const elicitSlot = (sessionAttributes, intentName, slots, slotToElicit, message) => {
    return {
        sessionAttributes,
        dialogAction: {
            type: 'ElicitSlot',
            intentName,
            slots,
            slotToElicit,
            message,
        },
    };
}

export const delegate = (sessionAttributes, slots) => {
    return {
        sessionAttributes,
        dialogAction: {
            type: 'Delegate',
            slots,
        },
    };
}