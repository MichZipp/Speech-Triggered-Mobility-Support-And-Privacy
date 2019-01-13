import { getUserProfile, getDocIds } from "./api";

const error_response = "Sorry, something went wrong, please try again!";

export const getUserName = (access_token, user_id) =>
    new Promise(function (resolve, reject) {
        var response;
        getUserProfile(access_token, user_id)
        .then( profile => {
            response = "Are you kidding me, your name is " + profile.vorname + " " + profile.name;
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
            response = "Actually you are in " + profile.location;
            resolve(response);
        })
        .catch( error => {
            response = "Error: " + error;
            reject(response);
        });
    });

export const getDocs = (access_token, user_id) => 
    new Promise(function (resolve, reject) {
        var docs = [];
        var response= "In furtwangen are the following doctors: "
        getDocIds(access_token, user_id)
        .then( ids => {
            const promises = [];
            for(var i in ids){
                 promises.push(getUserProfile(access_token, ids[i]));
            }
           
            Promise.all(promises)
            .then( docs => {
                for(var i in docs){
                    response += docs[i].vorname + " " + docs[i].name + ", ";
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
    });