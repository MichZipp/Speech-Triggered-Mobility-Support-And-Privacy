import { getUserProfile, getDocs } from "./api";

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