import { getActiveUserProfile, getUserLocation, getUserProfileName, getDocs } from "./api";

const error_response = "Sorry, something went wrong, please try again!";

export const getUserName = (access_token, profile_id) => 
    new Promise(function (resolve, reject) {
        try {
            var profile = getActiveUserProfile(access_token, profile_id);
            var response = "Are you kiddinng my, your name is: " + profile.firstname + " " + profile.lastname;
            resolve(response)
        }
        catch(error) {
            reject(error_response);
        }
    });

export const getUserProfileName = (access_token, profile_id) => 
    new Promise(function (resolve, reject) {
        try {
            var profile = getActiveUserProfile(access_token, profile_id);
            var response = "You Profile " + profile.profilename + "is currently activated!";
            resolve(response)
        }
        catch(error) {
            reject(error_response);
        }
    });

export const getUserLocation = (access_token, profile_id) => 
    new Promise(function (resolve, reject) {
        try {
            var profile = getActiveUserProfile(access_token, profile_id);
            var response = "Actually you are in " + profile.stadt;
            resolve(response)
        }
        catch(error) {
            reject(error_response);
        }
    });