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


const MOPComponent = () => {
  const [mopValue, setMopValue] = useState(0);
  const [showSubmit, setShowSubmit] = useState(true);
  const { enqueueSnackbar } = useSnackbar();

  useEffect(() => {
    // Fetch the GTD value from the server
    const fetchMOPValue = async () => {
      try {
        const user = JSON.parse(localStorage.getItem("userDetails"));
        if(user)

        {
        const response = user.employerId ? await axios.get(`${config.baseURL}/employer/${user.employerId}/employee/${user.id}`):await axios.get(`${config.baseURL}/employer/${user.id}`);
        const data = response.data;
        console.log(data);
        console.log(data.mop);
        setMopValue(data.mop);
      }
      } catch (error) {
        console.error("Error fetching MOP value:", error);
      }
    };

    fetchMOPValue();
  }, []);

  const handleChange = (event) => {
    setMopValue(event.target.value);
  };

  const handleSubmitMOP = async () => {
    console.log(`Selected MOP value: ${mopValue}`);
    // TODO: Add functionality to submit the selected MOP value to the server
    try {
      const user = JSON.parse(localStorage.getItem("userDetails"));
      var url = `${config.baseURL}/employer/${user.id}/updateMop/${mopValue}`;
      if(user.employerId) {
            url = `${config.baseURL}/employer/${user.employerId}/employee/${user.id}/updateMop/${mopValue}`;    
      }
      const response = await axios.put(url,{});
      const data = response.data;
      console.log(data);
      enqueueSnackbar("MOP value updated successfully", { variant: "success" });
    } catch (error) {
      enqueueSnackbar("Error updating MOP value", { variant: "error" });
    }
  

  
  };

  const handleCancel = async () => {
    // setMopValue(mopValue);
    try {
      const user = JSON.parse(localStorage.getItem("userDetails"));
      
      var url = `${config.baseURL}/employer/${user.id}/cancelMop`;
      
      if(user.employerId) {
            url = `${config.baseURL}/employer/${user.employerId}/employee/${user.id}/cancelMop`;    
      }
      const response = await axios.get(url,{});
      const data = response.data;
      console.log(data);
      setMopValue(data.mop);
      enqueueSnackbar("MOP value cancelled successfully", { variant: "success" });
    } catch (error) {
      enqueueSnackbar("Error cancelling MOP value", { variant: "error" });
    }
  };

  return (
    <div>
      <Typography variant="h6" sx={{ margin: '40px', textAlign: 'center' }}>Select MOP</Typography>
      <Select
        labelId="select-label"
        id="select"
        value={mopValue}
        onChange={handleChange}
        disabled={!showSubmit}
      >
        {[0, 1, 2, 3, 4, 5].map((value) => (
          <MenuItem key={value} value={value}>
            {value}
          </MenuItem>
        ))}
      </Select>
    
        <Button
          sx={{ minWidth: 120, m: 2 }}
          variant="contained"
          onClick={handleSubmitMOP}
        >
          Submit MOP
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

export default MOPComponent;
