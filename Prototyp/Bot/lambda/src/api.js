import fetch from "node-fetch";

const api_url = "http://192.52.32.250:3000/api/customers/";
const res_access_token = "/profiles?access_token=";
const res_docs =  "http://192.52.32.250:3000/api/customers?filter=%7B%22where%22%3A%20%7B%22Usertype%22%3A%201%7D%7D&access_token=";

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
export const getDocIds = (access_token) => 
    new Promise(function(resolve, reject) {
        var url = res_docs.concat(access_token);
        console.log("url: " + url)
        fetch(url)
        .then( data => data.json())
        .then( json => {    
            var ids = [];  
            for(var i in json){
                console.log("Doc: " + JSON.stringify(json[i].id));
                ids.push(json[i].id);
            }  
            console.log("Ids: " + ids);
            resolve(ids);
        })
        .catch( error => {
            reject(error);
        });
    }); 