import axios from 'axios';
import { getToken } from './AuthService';

const REST_API_BASE_URL = 'http://localhost:8080/api/taxrate'; // Replace with your actual API endpoint

axios.interceptors.request.use(function (config) {
    config.headers['Authorization'] = getToken()
    return config;
  }, function (error) {
    // Do something with request error
    return Promise.reject(error);
  });

export const getTaxRate = () => axios.get(REST_API_BASE_URL);


export const updateTaxRate = (taxRate) => axios.put(REST_API_BASE_URL, taxRate);
