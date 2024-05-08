document.addEventListener('DOMContentLoaded', function () {
    const form = document.getElementById('pdfForm');
    const textInput = document.getElementById('textInput'); // Get the input query box element

    form.addEventListener('submit', function (event) {
        event.preventDefault(); // Prevent the default form submission

        const formData = new FormData(form); // Create FormData object from the form
        formData.append('textInput', textInput.value); // Append the input query value

        // Send a POST request to the server with the form data
        fetch('/upload', {
            method: 'POST',
            body: formData
        })
        .then(response => {
            if (response.ok) {
                return response.blob(); // Return the response as a Blob object
            } else {
                throw new Error('Failed to upload file');
            }
        })
        .then(blob => {
            // Get the name of the input file
            const inputFileName = document.getElementById('pdfFile').files[0].name;
            // Construct the new file name with "highlighted" added
            const outputFileName = inputFileName.replace(/\.[^/.]+$/, "") + '_highlighted.pdf';

            // Create a URL for the Blob object
            const url = URL.createObjectURL(blob);

            // Create a link element to trigger the download
            const link = document.createElement('a');
            link.href = url;
            link.download = outputFileName; // Set the file name
            document.body.appendChild(link);

            // Click the link to trigger the download
            link.click();

            // Remove the link element from the DOM
            document.body.removeChild(link);
        })
        .catch(error => {
            console.error('Upload failed:', error);
        });
    });
});
