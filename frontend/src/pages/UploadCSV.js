import React, { useState } from "react";
import axios from "axios";
import { Typography, Box, Button } from "@mui/material";
import { useSnackbar } from "notistack";

const UploadCSV = ({ url }) => {
  const [selectedFile, setSelectedFile] = useState(null);
  const { enqueueSnackbar } = useSnackbar();

  const handleFileChange = (event) => {
    setSelectedFile(event.target.files[0]);
  };

  const handleUpload = () => {
    if (selectedFile) {
      const formData = new FormData();
      formData.append("file", selectedFile);      
      axios
        .post(url, formData, {
          headers: {
            "Content-Type": "multipart/form-data",
          },
        })
        .then((response) => {
          console.log("Upload successful:", response);
          // TODO: Handle successful upload
        })
        .catch((error) => {
          console.error("Error uploading file:", error);
          enqueueSnackbar(error.response.data, { variant: "error" });
          // TODO: Handle upload error
        });
    }
  };

  return (
    <div>
      <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
      <Typography variant="h6" sx={{ margin: '20px', textAlign: 'center' }}>Upload CSV</Typography>

        <input type="file" accept=".csv" onChange={handleFileChange} />
        <Button sx={{ m: 2 }} variant="contained" onClick={handleUpload}>
          Upload
        </Button>
      </Box>
    </div>
  );
};

export default UploadCSV;
