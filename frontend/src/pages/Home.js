import React from 'react';
import {  signOut } from "firebase/auth";
import {auth} from '../firebase';
import { useNavigate } from 'react-router-dom';
import { Typography, Box, Button } from "@mui/material";

const Home = () => {
    const navigate = useNavigate();
 
    const handleLogout = () => {     
        localStorage.setItem('user',null);
        navigate("/auth/login");          
        // signOut(auth).then(() => {
        // // Sign-out successful.
        //     // navigate("/");
        //     localStorage.setItem('user',null);
        //     console.log("Signed out successfully")
        // }).catch((error) => {
        // // An error happened.
        // });
    }
   
    return(
        <>
           <Button sx={{ m: 2 }} variant="contained" onClick={handleLogout}>
          Logout
        </Button>
        </>
    )
}
 
export default Home;