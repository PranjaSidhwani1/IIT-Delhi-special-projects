const { spawn } = require('child_process');
const path = require('path');
const express = require('express');
const multer = require('multer');
const fs = require('fs');

const app = express();
const port = 3000;

app.use(express.static(__dirname));

app.get('/', (req, res) => {
    res.sendFile(path.join(__dirname, 'index.html'));
});

const storage = multer.diskStorage({
    destination: function (req, file, cb) {
        cb(null, 'uploads/');
    },
    filename: function (req, file, cb) {
        cb(null, file.fieldname + '-' + Date.now() + path.extname(file.originalname));
    }
});

const upload = multer({ storage: storage });

app.post('/upload', upload.single('pdfFile'), (req, res) => {
    if (!req.file) {
        return res.status(400).send('Please upload a PDF file.');
    }

    const inputFile = req.file.path;
    const textInput = req.body.textInput; // Access text input from request body

    // Construct the output file name based on the input query
    const outputFileName = textInput.replace(/[^a-zA-Z0-9]/g, '_') + '.pdf';
    const outputFile = path.join(__dirname, outputFileName);

    const pythonProcess = spawn('python', ['pdf_processing_script.py', inputFile, outputFile, textInput]); // Pass text input as argument

    pythonProcess.on('close', (code) => {
        if (code === 0) {
            res.download(outputFile, outputFileName, (err) => { // Use outputFileName here
                if (err) {
                    console.error('Error downloading file:', err);
                    res.status(500).send('Error downloading file.');
                } else {
                    // Cleanup temporary files
                    fs.unlinkSync(inputFile);
                    fs.unlinkSync(outputFile);
                }
            });
        } else {
            console.error('Error processing PDF.');
            res.status(500).send('Error processing PDF.');
            // Cleanup temporary files
            fs.unlinkSync(inputFile);
        }
    });
});

app.listen(port, () => {
    console.log(`Server is running on http://localhost:${port}`);
});
