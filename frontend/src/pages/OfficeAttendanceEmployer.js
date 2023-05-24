import React, { useState, useEffect } from "react";
import { FormControl, InputLabel, Select, MenuItem, Button, Box, Typography, TextField } from "@mui/material";
import axios from "axios";
import MOPComponent from "../components/Mop";
import CapacityComponent from "../components/Capacity";

const OfficeAttendanceEmployer = () => {

  return (
    <div>
      <Typography variant="h4" sx={{ marginBottom: '20px', textAlign: 'center' }}>Office Attendance</Typography>
      <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
        <MOPComponent></MOPComponent>
        <CapacityComponent></CapacityComponent>
      </Box>
        

        

    </div>
  );
};

export default OfficeAttendanceEmployer;
