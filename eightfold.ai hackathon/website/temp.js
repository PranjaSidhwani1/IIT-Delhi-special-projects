const express = require('express');
const app = express();
const path = require('path');

// Serve static files from the 'public' directory
app.use(express.static(path.join(__dirname, 'public')));

// Define other route handlers...

app.listen(3000, () => {
    console.log('Server is running on http://localhost:3000');
});
