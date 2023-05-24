// Import the functions you need from the SDKs you need
import { initializeApp } from "firebase/app";
import { getAuth } from "firebase/auth";

const firebaseConfig = {
  apiKey: "AIzaSyB-F9S_WX-DsrX4ObdsSSS0rm4bNpzWdmg",
  authDomain: "wiors-275.firebaseapp.com",
  projectId: "wiors-275",
  storageBucket: "wiors-275.appspot.com",
  messagingSenderId: "262145091951",
  appId: "1:262145091951:web:dca8ffbe02fd6576d0a09a",
  measurementId: "G-M7N02X7BC4"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);

// Initialize Firebase Authentication and get a reference to the service
export const auth = getAuth(app);
export default app;
