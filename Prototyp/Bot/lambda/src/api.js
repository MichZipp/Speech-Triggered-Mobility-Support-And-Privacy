const access_token = "PhNcTP7GpCaPoXW2EqylcgbISnyT98YEMBAXNebJW3cFto9HlXBJgYye9zV9WYn7";
const url = "http://192.52.33.31:3000/api/profiles?access_token=";

export const getUserName = () => { 
    return new Promise(function(resolve, reject) {
        fetch(urt.concat(access_token))
        .then( data => {
            console.log(data);
            var user = data.json();
            
            var username = user.firstname + " " + lastname;
            resolve(username);
        })
        .catch( error => {
            reject(error);
        });
    });  
}

export const getUserProfileName = () => { 
    return new Promise(function(resolve, reject) {
        fetch(urt + access_token)
        .then( data => {
            var user = data.json();
            var profile = user.profileName;
            resolve(profile);
        })
        .catch( error => {
            reject(error);
        });
    });  
}