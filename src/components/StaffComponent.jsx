import React from 'react'
import { useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { createStaff, getUserByID, updateUserByID } from '../services/AuthService'

const StaffComponent = () => {

    const [name, setName] = useState('')
    const [username, setUsername] = useState('')
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')
    const { id } = useParams()

    const [errors, setErrors] = useState({})

    const navigate = useNavigate()

    useEffect(() => {
        if(id) {
            getUserByID(id).then((response) => {
                console.log(response.data)
                setName(response.data.name)
                setEmail(response.data.email)
                setPassword(response.data.password)
                setUsername(response.data.username)
            }).catch(error => {
                console.error(error)
            })
        }
    }, [id])

    function saveOrUpdateStaff(e) {
        e.preventDefault()
        const user= {name, username, email, password}
        console.log(user)

        setErrors({});

        if (id) {
            updateUserByID(id, user).then((response) => {
                console.log(response.data)
                navigate('/manage-staff')
            }).catch(error => {
                console.error(error)
                const errorsCopy = {... errors};
                errorsCopy.general = "Unable to edit Staff\nUsername or email already in use"
                setErrors(errorsCopy)
            })
        } else {
            createStaff(user).then((response) => {
                console.log(response.data)
                navigate('/manage-staff')
            }).catch(error => {
                console.error(error)
                const errorsCopy = {... errors};
                errorsCopy.general = "Unable to create Staff\nUsername or email already in use"
                setErrors(errorsCopy)
            })
        }
    }

    function pageTitle() {
        if (id) {
            return <h2 className='text-center'>Update Staff</h2>
        } else {
            return <h2 className='text-center'>Add Staff</h2>
        }
    }

  return (
    <div className='container'>
        <br /> <br />
        <div className='row'>
            <div className='card col-md-6 offset-md-3'>
                <div className='card-header'>
                { pageTitle() }
                </div>
                
                <div className='card-body'>
                    <form onSubmit={saveOrUpdateStaff}>
                        <div className='row mb-3'>
                            <label className='col-md-3 control-label' align="right">Name  </label>
                            <div className='col-md-9'>
                                <input
                                    type='text'
                                    name='name'
                                    className='form-control'
                                    required
                                    value={name}
                                    onChange={(e) => setName(e.target.value)}
                                >
                                </input>
                            </div>
                        </div>

                        <div className='row mb-3'>
                            <label className='col-md-3 control-label' align="right">Username  </label>
                            <div className='col-md-9'>
                                <input
                                    type='text'
                                    name='username'
                                    className='form-control'
                                    required
                                    value={username}
                                    onChange={(e) => setUsername(e.target.value)}
                                >
                                </input>
                            </div>
                        </div>

                        <div className='row mb-3'>
                            <label className='col-md-3 control-label' align="right">Email  </label>
                            <div className='col-md-9'>
                                <input
                                    type='email'
                                    name='email'
                                    className='form-control'
                                    required
                                    value={email}
                                    onChange={(e) => setEmail(e.target.value)}
                                >
                                </input>
                            </div>
                        </div>

                        {!id && (
                            <div className='row mb-3'>
                                <label className='col-md-3 control-label' align="right">Password  </label>
                                <div className='col-md-9'>
                                    <input
                                        type='password'
                                        name='password'
                                        className='form-control'
                                        required
                                        value={password}
                                        onChange={(e) => setPassword(e.target.value)}
                                    >
                                    </input>
                                </div>
                            </div>
                        )}

                        <br></br>

                        <button type='submit' className='btn btn-success' >Submit</button>
                    </form>
                </div> 
                    {errors.general && (
                                    <div className="alert alert-danger mt-3" role="alert" style={{ whiteSpace: 'pre-wrap' }}>
                                        {errors.general}
                                    </div>
                            )}
            </div>
        </div>
    </div>
  )
}

export default StaffComponent