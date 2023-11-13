/*
async function uploadToServer (formObj) {

    console.log("upload to server......")
    const response = await axios({
        method: 'post',
        url: '/upload',
        data: formObj,
        headers: {
            'Content-Type': 'multipart/form-data',
        },
    });


    // const response = await axios.post('/upload',
    //     formObj,
    //     { // 객체 리터럴(key-value)
    //         headers: {
    //             'Content-Type': 'multipart/form-data',
    //         },
    //     }
    // );


    console.log('여기는 upload.js', response.data)

    return response.data
}
*/


async function uploadToServer(formObj) {
    console.log("upload to server......")
    // CSRF 토큰과 헤더 이름을 메타 태그에서 읽습니다.
    const csrfToken = document.querySelector("meta[name='_csrf']").getAttribute("content");
    const csrfHeaderName = document.querySelector("meta[name='_csrf_header']").getAttribute("content");

    const response = await axios({
        method: 'post',
        url: '/upload',
        data: formObj,
        headers: {
            'Content-Type': 'multipart/form-data',
            [csrfHeaderName]: csrfToken,  // CSRF 토큰을 헤더에 추가합니다.
        },
    });

    console.log('여기는 upload.js', response.data)
    return response.data
}


// async function removeFileToServer(uuid, fileName){
//     const response = await axios.delete(`/remove/${uuid}_${fileName}`)
//     return response.data
// }

async function removeFileToServer(uuid, fileName){
    // CSRF 토큰과 헤더 이름을 메타 태그에서 읽습니다.
    const csrfToken = document.querySelector("meta[name='_csrf']").getAttribute("content");
    const csrfHeaderName = document.querySelector("meta[name='_csrf_header']").getAttribute("content");

    const response = await axios({
        method: 'delete',
        url: `/remove/${uuid}_${fileName}`,
        headers: {
            [csrfHeaderName]: csrfToken,  // CSRF 토큰을 헤더에 추가합니다.
        },
    });

    return response.data;
}
