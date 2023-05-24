import React, { useState } from "react";
import { FormControl, InputLabel, Select, MenuItem, Button, Box, Typography, RadioGroup, Radio, FormControlLabel } from "@mui/material";
import DateRangePicker from 'rsuite/DateRangePicker';
import MetricsReport from "./MetricsReport";
import axios from "axios";
import config from "../config";
import { useSnackbar } from "notistack";

const Dashboard = () => {
  const { enqueueSnackbar } = useSnackbar();
  const [selectedDateRange, setSelectedDateRange] = useState([]);

  const [pickedWeekData, setPickedWeekData] = useState({
    week: "",
    overAllAttendanceMeetRate: 0,
    employeeComplianceRate: 0,
    additionalSeatsRequired: 0,
  });



  const handleDateRangeChange = (value) => {
    setSelectedDateRange(value);
    console.log('&&', value)
  };

  // const handlePickWeekChange = (value) => {
  //   setPickedWeekData(value);
  // }


  const handleSubmit = (event) => {
    event.preventDefault();
    console.log('Selected Date Range:', selectedDateRange);
    const startDate = new Date(selectedDateRange[0]);
    const endDate = new Date(selectedDateRange[1]);

    // Extract the dates
    const options = { timeZone: "America/Los_Angeles" };
    const startDateParts = startDate.toLocaleDateString("en-US", options).split(",")[0].split("/");
    const endDateParts = endDate.toLocaleDateString("en-US", options).split(",")[0].split("/");
    const startDateFormatted = `${startDateParts[2]}-${startDateParts[0].padStart(2, "0")}-${startDateParts[1].padStart(2, "0")}`;
    const endDateFormatted = `${endDateParts[2]}-${endDateParts[0].padStart(2, "0")}-${endDateParts[1].padStart(2, "0")}`;

    console.log("Start Date:", startDateFormatted);
    console.log("End Date:", endDateFormatted);

    // TODO: Add functionality to submit the selected value to the server
    const user = JSON.parse(localStorage.getItem("userDetails"));
    axios.get(`${config.baseURL}/employer/${user.employerId}/employee/${user.id}/getAnalytics`, {
      params: {
        startDate: startDateFormatted,
        endDate: endDateFormatted
      }
    },)
      .then((response) => {
        console.log("Dashboard response:", response);
        setPickedWeekData(response.data)
        enqueueSnackbar("Success", { variant: "success" });
        // TODO: Handle successful upload
      })
      .catch((error) => {
        console.error("Error uploading file:", error);
        enqueueSnackbar(error.response.data, { variant: "error" });
      });
  };


  return (
    <div >
      <Typography variant="h4" sx={{ marginBottom: '20px', textAlign: 'center' }}>Dashboard</Typography>

      <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
        <FormControl sx={{ minWidth: 120 }}>

          <Typography variant="h6" sx={{ margin: '40px', textAlign: 'center' }}>Week Selection</Typography>

          <DateRangePicker
            hoverRange="week"
            ranges={[]}
            oneTap
            value={selectedDateRange}
            onChange={handleDateRangeChange}
          />

        </FormControl>

        <Button sx={{ minWidth: 120, m: 2 }} variant="contained" onClick={handleSubmit}>
          Submit
        </Button>

        <MetricsReport pickedWeekData={pickedWeekData} />

      </Box>

    </div>
  );
};

export default Dashboard;