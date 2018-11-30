 export const close = (sessionAttributes, fulfillmentState, message) => {
    return {
        sessionAttributes,
        dialogAction: {
            type: "Close",
            fulfillmentState,
            message,
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