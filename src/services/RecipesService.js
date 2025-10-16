import axios from "axios"
import { getToken } from './AuthService';


/** Base URL for the Recipe API - Correspond to methods in Backend's Recipe Controller. */
const REST_API_BASE_URL = "http://localhost:8080/api/recipes"

axios.interceptors.request.use(function (config) {
    config.headers['Authorization'] = getToken()
    return config;
  }, function (error) {
    // Do something with request error
    return Promise.reject(error);
  });


/** GET Recipes - lists all recipes */
export const listRecipes = () => axios.get(REST_API_BASE_URL)

/** POST Recipe - creates a new recipe */
export const createRecipe = (recipe) => axios.post(REST_API_BASE_URL, recipe)

/** GET Recipe - gets a single recipe by id */
export const getRecipe = (id) => axios.get(REST_API_BASE_URL + "/" + id)

/** DELETE Recipe - deletes the recipe with the given id */
export const deleteRecipe = (id) => axios.delete(REST_API_BASE_URL + "/" + id)

/** PUT Recipe - updates given recipe */
export const updateRecipe = (id, recipe) => axios.put(REST_API_BASE_URL + "/" + id, recipe)