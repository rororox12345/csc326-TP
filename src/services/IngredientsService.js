import axios from "axios"
import { getToken } from './AuthService';


/** Base URL for the Ingredient API - Correspond to methods in Backend's Ingredient Controller. */
const REST_API_BASE_URL = "http://localhost:8080/api/ingredients"


axios.interceptors.request.use(function (config) {
    config.headers['Authorization'] = getToken()
    return config;
  }, function (error) {
    // Do something with request error
    return Promise.reject(error);
  });

/** POST Ingredient - creates a new ingredient */
export const createIngredient = (ingredient) => axios.post(REST_API_BASE_URL, ingredient)
