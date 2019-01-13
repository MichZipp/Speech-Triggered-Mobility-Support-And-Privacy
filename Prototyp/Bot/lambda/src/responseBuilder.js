import { getUserProfile, getDocs } from "./api";

const error_response = "Sorry, something went wrong, please try again!";

export const getUserName = (access_token, user_id) =>
    new Promise(function (resolve, reject) {
        var response;
        getUserProfile(access_token, user_id)
        .then((profile) => {
            console.log("Response:" + profile);
            response = "Are you kidding me, your name is " + profile.vorname + " " + profile.name;
            resolve(response);
        })
        .catch( error => {
            response = "Error: " + error;
            reject(response);
        });
    });


export const getUserLocation = (access_token, profile_id) => 
    new Promise(function (resolve, reject) {
        try {
            var profile = getUserProfile(access_token, profile_id);
            var response = "Actually you are in " + profile.location;
            resolve(response)
        }
        catch(error) {
            reject(error_response);
        }
    });