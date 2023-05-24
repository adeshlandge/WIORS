import React, { useState, useEffect } from "react";
import {
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Button,
  Box,
  Typography,
} from "@mui/material";
import axios from "axios";
import config from "../config";
import { useSnackbar } from "notistack";

const GTDComponent = () => {
  const [selectedGTD, setSelectedGTD] = useState(null);
  const { enqueueSnackbar } = useSnackbar();


  useEffect(() => {
    // Fetch the GTD value from the server
    const fetchValue = async () => {
      try {
        const user = JSON.parse(localStorage.getItem("userDetails"));
        if(user)

        {
        const response =  await axios.get(`${config.baseURL}/employer/${user.employerId}/employee/${user.id}`);
        const data = response.data;
        console.log(data);
        console.log(data.gtd);
        if(data.gtd === null) { data.gtd = "None"; }
        setSelectedGTD(data.gtd);
      }
      } catch (error) {
        console.error("Error fetching MOP value:", error);
      }
    };

    fetchValue();
  }, []);
  const handleGTDSelection = (event) => {
    setSelectedGTD(event.target.value);
  };

  const handleSubmitGTD = async () => {
    console.log(`Selected GTD value: ${selectedGTD}`);
    // TODO: Add functionality to submit the selected GTD value to the server
   
    try {
      const user = JSON.parse(localStorage.getItem("userDetails"));
      var url = `${config.baseURL}/employer/${user.id}/updateMop/${selectedGTD}`;
      if(user.employerId) {
            url = `${config.baseURL}/employer/${user.employerId}/employee/${user.id}/updateGtd/${selectedGTD}`;    
      }
      const gtd = selectedGTD;
      const response = await axios.put(url,{});
      

      const data = response.data;
      console.log(data);
      enqueueSnackbar("GTD value updated successfully", { variant: "success" });
    } catch (error) {
      enqueueSnackbar("Error updating GTD value:  " + error.response.data, { variant: "error" });
    }
    
  };

   const handleCancel = async () => {

    try {
      const user = JSON.parse(localStorage.getItem("userDetails"));
    
      const url = `${config.baseURL}/employer/${user.employerId}/employee/${user.id}/cancelGtd`;    
      const response = await axios.delete(url,{});
      const data = response.data; 
      console.log(data);

      enqueueSnackbar("GTD value cancelled successfully", { variant: "success" });
    } catch (error) {
      enqueueSnackbar("Error cancelling GTD value", { variant: "error" });
    }
  };  

  return (
    <div>
      <Typography variant="h6" sx={{ margin: '40px', textAlign: 'center' }}>Select GTD</Typography>
      <Select
        labelId="gtd-select-label"
        id="gtd-select"
        value={selectedGTD}
        onChange={handleGTDSelection}
      >
        <MenuItem value = "None">None</MenuItem>
        {["MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"].map(
          (value) => (
            <MenuItem key={value} value={value}>
              {value}
            </MenuItem>
          )
        )}
      </Select>
      <Button
        sx={{ minWidth: 120, m: 2 }}
        variant="contained"
        onClick={handleSubmitGTD}
      >
        Submit GTD
      </Button>

      <Button
          sx={{ minWidth: 120, m: 2 }}
          variant="contained"
          onClick={handleCancel}
        >
          Cancel
        </Button>
    </div>
  );
};


export default GTDComponent;
