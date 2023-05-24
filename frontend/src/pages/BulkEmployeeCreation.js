import React, { useState } from "react";
import UploadCSV from "./UploadCSV";
import { Typography, Box } from "@mui/material";
import { useEffect } from "react";
import config from "../config/index";

const BulkEmployeeCreation = () => {

  const [url, setUrl] = useState(null)

  useEffect(() => {
    const user = JSON.parse(localStorage.getItem("userDetails"));    
    setUrl(`${config.baseURL}/employer/${user.id}/registeremployees/employees/upload`);    
  }, []);

  const handleUploadSuccess = (response) => {
    // TODO: Handle successful upload
    console.log("Bulk employee creation successful:", response);
  };

  const handleUploadError = (error) => {
    // TODO: Handle upload error
    console.error("Error uploading bulk employee creation:", error);
  };

  return (
    <div>
      <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
        <Typography variant="h6" sx={{ margin: '30px', textAlign: 'center' }}>Bulk Employee Creation</Typography>
        <UploadCSV url={url} onUploadSuccess={handleUploadSuccess} onUploadError={handleUploadError} />
      </Box>
    </div>
  );
};

export default BulkEmployeeCreation;
