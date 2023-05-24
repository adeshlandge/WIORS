import React, { useState, useEffect } from "react";
import {
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Button,
  Box,
  Typography,
  TextField
} from "@mui/material";
import axios from "axios";
import config from "../config";
import { useSnackbar } from "notistack";

const CapacityComponent = () => {
  const [capacity, setCapacity] = useState(0);
  const [showSubmit, setShowSubmit] = useState(true);
  const { enqueueSnackbar } = useSnackbar();

  useEffect(() => {
    // Fetch the GTD value from the server
    const fetchCapacity = async () => {
      try {
        const user = JSON.parse(localStorage.getItem("userDetails"));
        if(user)
        {
        console.log("user is ", user)
        const response = await axios.get(`${config.baseURL}/employer/${user.id}`);
        const data = response.data;
        console.log(data);
        console.log('capacity is :',data.capacity);
        setCapacity(data.capacity);
      }
      } catch (error) {
        console.error("Error fetching capacity value:", error);
      }
    };

    fetchCapacity();
  }, []);

  const handleChange = (event) => {
    setCapacity(event.target.value);
  };

  const handleSubmitMOP = async () => {
    console.log(`Selected capacity value: ${capacity}`);
    // TODO: Add functionality to submit the selected capacity value to the server
    try {
      const user = JSON.parse(localStorage.getItem("userDetails"));
      const response = await axios.put(`${config.baseURL}/employer/${user.id}/updateCapacity/${capacity}`,{});
      const data = response.data;
      console.log(data);
      enqueueSnackbar("Capacity value updated successfully", { variant: "success" });
    } catch (error) {
      enqueueSnackbar("Error updating capacity value:  " + error.response.data, { variant: "error" });
    }
   
  
  };

  const handleCancel = () => {
    // setCapacity(capacity);
   
  };

  return (
    <div>
      <Typography variant="h6" sx={{ margin: '40px', textAlign: 'center' }}>Select capacity</Typography>
      <TextField
                  label="Seating capacity"
                  type="number"
                  fullWidth
                  required
                  margin="normal"
                  value={capacity}
                  onChange={(e) => setCapacity(e.target.value)}
                  inputProps={{ min: 3, max: 100 }}
                  error={capacity < 3 || capacity > 100}
                  helperText={
                    capacity < 3 || capacity > 100
                      ? "Seating capacity must be between 3 and 100."
                      : ""
                  }
                />
    
        <Button
          sx={{ minWidth: 120, m: 2 }}
          variant="contained"
          onClick={handleSubmitMOP}
        >
          Update capacity
        </Button>

        {/* <Button
          sx={{ minWidth: 120, m: 2 }}
          variant="contained"
          onClick={handleCancel}
        >
          Cancel
        </Button> */}
      
    </div>
  );
};

export default CapacityComponent;
