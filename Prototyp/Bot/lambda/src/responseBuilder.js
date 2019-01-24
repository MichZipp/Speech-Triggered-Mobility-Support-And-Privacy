import { getUserProfile, getDocIds } from "./api";
import { close, elictSlot, delegate } from "./helpers";

const weekdays = ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"];
const times = [1,2,3,4,5,6,7,8,9,10,11,12];

export const getUserName = (access_token, user_id) =>
    new Promise(function (resolve, reject) {
        var response;
        getUserProfile(access_token, user_id)
        .then( profile => {
            response = "Are you kidding me, your name is " + profile.vorname + " " + profile.name  + ".";
            resolve(response);
        })
        .catch( error => {
            response = "Error: " + error;
            reject(response);
        });
    });


export const getUserLocation = (access_token, user_id) => 
    new Promise(function (resolve, reject) {
        var response;
        getUserProfile(access_token, user_id)
        .then( profile => {
            response = "Actually you are in " + profile.location + ".";
            resolve(response);
        })
        .catch( error => {
            response = "Error: " + error;
            reject(response);
        });
    });

export const getDocs = (access_token, user_id) => 
    new Promise(function (resolve, reject) {
        var response= "In furtwangen are the following doctors: ", location, docsNumber = 0;
        getUserProfile(access_token, user_id)
        .then( profile => {
            location = profile.location;
            console.log("Location: " + profile.location);

            getDocIds(access_token)
            .then( ids => {
                const promises = [];
                for(var i in ids){
                    promises.push(getUserProfile(access_token, ids[i]));
                }
            
                Promise.all(promises)
                .then( docs => {
                    for(var i in docs){
                        if(docs[i].location === location){
                            docsNumber++;
                            response += docs[i].vorname + " " + docs[i].name;
                            if(docs.length !== docsNumber){
                                response += ", ";
                            } 
                        }                    
                    }

                    if(docsNumber === 0){
                        response = "Unfortunately, no doctors were found nearby.";
                    }

                    console.log("Response: " + response);            
                    resolve(response);
                })
                .catch( error => {
                    console.log(error);
                    reject(error)
                });            
            })
            .catch( error => {
                console.log(error);
                reject(error)
            });
        })
        .catch( error => {
            response = "Error: " + error;
            reject(response);
        });  
    });

export const getNewDocAppointmet = (access_token, user_id, callback, sessionAttributes) => 
    new Promise(function (resolve, reject) {
        var response= "", location, scalaMobile, pickUp, docsNumber = 0, docNames = [];
        getUserProfile(access_token, user_id)
        .then( profile => {
            location = profile.location;
            scalaMobile = profile.scalamobile;
            pickUp = profile.pickup;

            getDocIds(access_token)
            .then( ids => {
                const promises = [];
                for(var i in ids){
                    promises.push(getUserProfile(access_token, ids[i]));
                }
            
                Promise.all(promises)
                .then( docs => {
                    for(var i in docs){
                        if(docs[i].location === location){
                            docsNumber++;
                            docNames.push(docs[i].vorname + " " + docs[i].name + ", ");
                        }                    
                    }

                    if(docsNumber === 0){
                        response = "Unfortunately, no doctors were found nearby!";
                    }else{
                        var randomDocNumber = getRandomInt(0, docNames.length-1);
                        var randomDayNumber = getRandomInt(0, weekdays.length-1);
                        var randomTimeNumber = getRandomInt(0, times.length-1);

                        response = "I found an appointment to see "
                            + docNames[randomDocNumber] 
                            + " next " 
                            + weekdays[randomDayNumber]
                            + " at "
                            + times[randomTimeNumber]
                            + " o'clock!";

                        if(pickUp === 1){
                            response += "A car for you is reservated and the car will pick you up 30 minutes before your appointment!";
                        }

                        if(scalaMobile === 1){
                            response += "In addition, Scala Mobile will come to help you walk down the stairs.";
                        }

                        response += "I wish you a comfortable doctor visit!";
                    }

                    console.log("Response: " + response);            
                    callback(close(sessionAttributes, response)); 
                })
                .catch( error => {
                    console.log(error);
                    reject(error)
                });            
            })
            .catch( error => {
                console.log(error);
                reject(error)
            });
        })
        .catch( error => {
            response = "Error: " + error;
            reject(response);
        });  
    });


const getRandomInt = (min, max) => {
    min = Math.ceil(min);
    max = Math.floor(max);
    return Math.floor(Math.random() * (max - min + 1)) + min;
}