import fetch from "node-fetch";

const api_url = "http://192.52.32.250:3000/api/customers/";
const res_access_token = "/profiles?access_token=";


const res_profiles_private = "profiles?filter=%7B%22profiletype%22%3A%200%7D";
const res_profiles_docs = "profiles?filter=%7B%22profiletype%22%3A%201%7D";
const res_profiles_cars = "profiles?filter=%7B%22profiletype%22%3A%202%7D";
const res_calendar_begin = "calendars?filter=%7B%22profileId%22%3A16%7D"
const res_calendar_end = "%7D"
const res_location_begin = "locations?filter=%7B%22profileId%22%3A16%7D"
const res_location_end = "%7D"

/**
 * Returns the profile of the user
 */
export const getUserProfile = (access_token, user_id) => 
    new Promise(function(resolve, reject) {
        var url = api_url.concat(user_id)
        .concat(res_access_token)
        .concat(access_token);
        console.log(url);
        fetch(url)
        .then( data => data.json())
        .then( json => {        
            resolve(json[0]);
        })
        .catch( error => {
            reject(error);
        });
    }); 


/**
 * Returns all profiles of docs
 */
/*export const getDocProfiles = (access_token) => 
    new Promise(function(resolve, reject) {
        fetch(server_url.concat(res_profiles_docs)
            .concat(res_user_access_token)
            .concat(access_token))
        .then( data => {
            console.log(data);
            var docs = data.json();
            resolve(docs);
        })
        .catch( error => {
            reject(error);
        });
    }); 

/**
 * Returns all profiles of car rentals
 */
/*export const getDocProfiles = (access_token) => 
    new Promise(function(resolve, reject) {
        fetch(server_url.concat(res_profiles_cars)
            .concat(res_user_access_token)
            .concat(access_token))
        .then( data => {
            console.log(data);
            var cars = data.json();
            resolve(docs);
        })
        .catch( error => {
            reject(error);
        });
    }); */

/**
 * Returns all profiles of private user
 */
/*export const getPrivateProfiles = (access_token) => 
    new Promise(function(resolve, reject) {
        fetch(server_url.concat(res_profiles_private)
            .concat(res_user_access_token)
            .concat(access_token))
        .then( data => {
            console.log(data);
            var profiles = data.json();
            resolve(profiles);
        })
        .catch( error => {
            reject(error);
        });
    });*/

/**
 * Returns the calendar of a specific user profile
 */
/*export const getCalendar = (access_token, profile_id) => 
    new Promise(function(resolve, reject) {
        fetch(server_url.concat(res_calendar_begin)
            .concat(profile_id)
            .concat(res_calendar_end)
            .concat(res_user_access_token)
            .concat(access_token))
        .then( data => {
            console.log(data);
            var calendar = data.json();
            resolve(calendar);
        })
        .catch( error => {
            reject(error);
        });
    });*/

    

