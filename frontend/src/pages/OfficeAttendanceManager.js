import React, { useState } from "react";
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
import MOPComponent from "../components/Mop";
import GTDComponent from "../components/GTD";



const OfficeAttendanceManager = () => {
  return (
    <div>
      <Typography variant="h4" sx={{ marginBottom: '20px', textAlign: 'center' }}>Office Attendance</Typography>

      <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
        <FormControl sx={{ minWidth: 120 }}>
          {/* MOP Component */}
          <MOPComponent />

          {/* GTD Component */}
          <GTDComponent />
        </FormControl>
      </Box>
    </div>
  );
};

export default OfficeAttendanceManager;
