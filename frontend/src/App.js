import Button from '@mui/material/Button';
import { createTheme } from './theme';
import { ThemeProvider } from '@mui/material/styles';
import {BrowserRouter as Router, Route, Routes, Navigate } from "react-router-dom";
import EmployeeLayout from './layouts/EmployeeLayout';
import EmployerLayout from './layouts/EmployerLayout';
import ManagerLayout from './layouts/ManagerLayout';
import PrivateRoute from './components/PrivateRoute';
import AuthLayout from './layouts/AuthLayout';
import Home from './pages/Home';
import Login from './pages/Login';
import NotFound from './pages/NotFound';
import SignUp from './pages/SignUp';
import Dashboard from './pages/Dashboard';
import Settings from './pages/Settings';
import OfficeAttendanceManager from './pages/OfficeAttendanceManager';
import OfficeAttendanceEmployer from './pages/OfficeAttendanceEmployer';

import BulkReservation from './pages/BulkReservation';
import BulkEmployeeCreation from './pages/BulkEmployeeCreation';
import React, { useState, useEffect } from 'react';
import { onAuthStateChanged } from "firebase/auth";
import { auth } from './firebase';
import {  useNavigate } from 'react-router-dom';
import { SnackbarProvider ,useSnackbar} from 'notistack';
import SeatReservation from './pages/SeatReservation/SeatReservation';
import { LocalizationProvider } from '@mui/x-date-pickers';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs'
import { useLocation } from "react-router-dom";
import ReservationTable from './pages/ReservationTable';


const theme = createTheme();
const App = () => {
  const [user, setUser] = useState(null);

  const navigate = useNavigate();
  const location = useLocation();
  const currentPath = location.pathname;
  const isSignUp = currentPath.includes("signup");
 
  const { enqueueSnackbar } = useSnackbar();

  function handleError(error) {
    enqueueSnackbar("error", { variant: 'error' });
  }

  useEffect(()=>{
    onAuthStateChanged(auth, (user) => {
      //localStorage.setItem('user', JSON.stringify(user));

        if (user) {
          // User is signed in, see docs for a list of available properties
          // https://firebase.google.com/docs/reference/js/firebase.User
          const uid = user.uid;
          console.log("uid", uid)
        } else if (!isSignUp) {
          // User is signed out
          // ...
          navigate("/auth/login")
          // localStorage.setItem('user',null);
          console.log("user is logged out")
        }} , (error) => handleError(error));
    
}, [])
     

  return (
    <ThemeProvider theme={theme}>
 <LocalizationProvider dateAdapter={AdapterDayjs}>
      <SnackbarProvider maxSnack={3} anchorOrigin={{ vertical: 'top', horizontal: 'right' }}>
      <Routes>
      <Route  exact path="/" element={ <Navigate to="/auth/login" />} />

        <Route path="/auth" element={<AuthLayout />}>
          <Route path="login" element={<Login />} />
          <Route path="signup" element={<SignUp />} />
        </Route>
      
        <Route path="/employee" element={<EmployeeLayout></EmployeeLayout>}>
          <Route path="home"  element={ <Home />} />
          <Route path="dashboard" element={<Dashboard />} />
          <Route path="settings" element={<Settings />} />
       
          <Route path="reservations" element={<ReservationTable />} />

        <Route path="seatreservation" element={<SeatReservation />} />
          <Route path="*" element={<NotFound />} />
        </Route>

        <Route path="/manager" element={<ManagerLayout></ManagerLayout>}>
          <Route path="home"  element={ <Home />} />
          <Route path="dashboard" element={<Dashboard />} />
          <Route path="settings" element={<Settings />} />
          <Route path="officeattendance" element={<OfficeAttendanceManager/>} />
          <Route path="reservations" element={<ReservationTable />} />
        <Route path="seatreservation" element={<SeatReservation />} />
          <Route path="*" element={<NotFound />} />
        </Route>
        
        <Route path="/employer" element={<EmployerLayout/>}>
           <Route path="home" element={<Home />} />
        <Route path="dashboard" element={<Dashboard />} />
        <Route path="settings" element={<Settings />} />
        <Route path="officeattendance" element={<OfficeAttendanceEmployer />} />
        <Route path="bulkreservation" element={<BulkReservation />} />
        <Route path="bulkemployeecreation" element={<BulkEmployeeCreation />} />
        <Route path="*" element={<NotFound />} />
      
        </Route>
        <Route path="*" element={<NotFound />} />
      </Routes>
      </SnackbarProvider>
      </LocalizationProvider>
    </ThemeProvider>
  );
};

export default App;