import React, { useState } from "react";
import {
  signInWithEmailAndPassword,
  signInWithPopup,
  GoogleAuthProvider,
} from "firebase/auth";
import { auth } from "../firebase";
import { NavLink, useNavigate } from "react-router-dom";
import { TextField, Button, Typography, Link, Box } from "@mui/material";
import {
  FormControl,
  FormControlLabel,
  Radio,
  RadioGroup,
} from "@mui/material";
import axios from "axios";
import config from "../config/index.js";
import { useSnackbar } from "notistack";
const Login = () => {
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [userType, setUserType] = useState("employer");
  const { enqueueSnackbar } = useSnackbar();

  const googleProvider = new GoogleAuthProvider();

  const onLogin = async (e) => {
    e.preventDefault();
    try {
      // localStorage.setItem('user', JSON.stringify(user));
      // localStorage.setItem('role', JSON.stringify(role));
      if (userType === "employer") {
        const response = await axios.post(`${config.baseURL}/login/employer`, {
          email,
          password,
        });
        console.log(response.data);
        if (response.status === 200) {
          enqueueSnackbar("Login Successful", { variant: "success" });
          localStorage.setItem("userDetails", JSON.stringify(response.data));
          navigate("/employer/home");
        }
      } else {
        const response = await axios.post(`${config.baseURL}/login/employee`, {
          email,
          password,
        });
        console.log(response.data);
        if (response.status === 200) {
          enqueueSnackbar("Login Successful", { variant: "success" });
          localStorage.setItem("userDetails", JSON.stringify(response.data));
          if (response.data.reports.length > 0) {
            navigate("/manager/home");
          } else {
            navigate("/employee/home");
          }
        }
      }

      // console.log(user);
    } catch (error) {
      enqueueSnackbar("Login Failed: " + error.response.data, { variant: "error" });

      const errorCode = error.code;
      const errorMessage = error.message;
      console.log(errorCode, errorMessage);
    }
  };

  const onGoogleLogin = (e) => {
    signInWithPopup(auth, googleProvider)
      .then(async (userCredential) => {
        
        // Signed in
        
        const user = userCredential.user;
        console.log(user);
        const accesstoken = userCredential.accessToken;
       
        try {
          // localStorage.setItem('user', JSON.stringify(user));
          // localStorage.setItem('role', JSON.stringify(role));
          if (userType === "employer") {
            const response = await axios.post(`${config.baseURL}/login/employer`, {
              email :user.email,
              password:"password",
              google:true
            });
            console.log(response.data);
            if (response.status === 200) {
              enqueueSnackbar("Login Successful", { variant: "success" });
              localStorage.setItem("userDetails", JSON.stringify(response.data));
              navigate("/employer/home");
            }
          } else {
            const response = await axios.post(`${config.baseURL}/login/employee`, {
              email :user.email,
              password:"password",
              google:true
            });
            console.log(response.data);
            if (response.status === 200) {
              enqueueSnackbar("Login Successful", { variant: "success" });
              localStorage.setItem("userDetails", JSON.stringify(response.data));
              if (response.data.reports.length > 0) {
                navigate("/manager/home");
              } else {
                navigate("/employee/home");
              }
            }
          }
    
          // console.log(user);
        } catch (error) {
          enqueueSnackbar("Login Failed: " + error.response.data, { variant: "error" });

          navigate("/auth/signup")
    
          const errorCode = error.code;
          const errorMessage = error.message;
          console.log(errorCode, errorMessage);
        }
      })
      .catch((error) => {
        const errorCode = error.code;
        const errorMessage = error.message;
        console.log(errorCode, errorMessage);
      });
  };

  return (
    <Box
      sx={{
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        height: "100vh",
      }}
    >
      <Box
        sx={{
          width: "90%",
          maxWidth: "400px",
          padding: "20px",
          boxShadow: "0px 0px 2px rgba(0, 0, 0, 0.25)",
        }}
      >
        <Typography
          variant="h4"
          sx={{ marginBottom: "20px", textAlign: "center" }}
        >
          Login
        </Typography>
        <Box component="form" onSubmit={onLogin}>
          <TextField
            id="email-address"
            label="Email address"
            type="email"
            fullWidth
            required
            margin="normal"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
          <TextField
            id="password"
            label="Password"
            type="password"
            fullWidth
            required
            margin="normal"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
          <FormControl component="fieldset">
            <Typography sx={{ marginTop: "10px", marginBottom: "10px" }}>
              Select user type:
            </Typography>
            <RadioGroup
              aria-label="user-type"
              name="user-type"
              value={userType}
              onChange={(e) => setUserType(e.target.value)}
            >
              <FormControlLabel
                value="employee"
                control={<Radio />}
                label="Employee"
              />
              <FormControlLabel
                value="employer"
                control={<Radio />}
                label="Employer"
              />
            </RadioGroup>
          </FormControl>
          <Button
            variant="contained"
            color="primary"
            type="submit"
            fullWidth
            sx={{ marginTop: "20px" }}
          >
            Login
          </Button>
          <Button
            variant="contained"
            color="secondary"
            onClick={onGoogleLogin}
            fullWidth
            sx={{ marginTop: "10px" }}
          >
            Google Login
          </Button>
        </Box>
        <Typography
          variant="body2"
          sx={{ marginTop: "20px", textAlign: "center" }}
        >
          No account yet? <Link href="/auth/signup">Sign up</Link>
        </Typography>
      </Box>
    </Box>
  );
};

export default Login;
