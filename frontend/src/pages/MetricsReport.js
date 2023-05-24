import React from "react";
import { Typography, Box, Card, CardContent } from "@mui/material";

const MetricsReport = ({ pickedWeekData }) => {
  return (
    <div>
      <Typography variant="h6" sx={{ margin: '30px', textAlign: 'center' }}>Metrics Report</Typography>
      <Box sx={{ display: 'flex', justifyContent: 'center' }}>
        <Card sx={{ minWidth: 300 }}>
          <CardContent>
            {/* <Typography variant="h6">Picked Week: {pickedWeekData.week}</Typography> */}
            <Typography variant="body1" sx={{ marginTop: '20px' }}>Overall Attendance Meet Rate: {pickedWeekData.overAllAttendanceMeetRate}%</Typography>
            <Typography variant="body1">Employee Compliance Rate: {pickedWeekData.employeeComplianceRate}%</Typography>
            <Typography variant="body1">Additional Seats Needed: {pickedWeekData.additionalSeatsRequired}</Typography>
          </CardContent>
        </Card>
      </Box>
    </div>
  );
};

export default MetricsReport;
