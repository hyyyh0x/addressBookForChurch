import axios from 'axios';

// Fetch all users
export const fetchAllUsers = async () => {
  try {
    const response = await axios.get('/users');
    return response.data;
  } catch (error) {
    console.error('Error fetching users:', error);
    throw error;
  }
};

// Convert file to byte array
const convertFileToBytes = (file) => {
  return new Promise((resolve, reject) => {
    if (!file) {
      reject("제공된 파일이 없습니다.");
      return;
    }

    const reader = new FileReader();
    reader.addEventListener("load", () => {
      const arrayBuffer = reader.result;
      const byteArray = new Uint8Array(arrayBuffer);
      resolve(Array.from(byteArray));
    });

    reader.addEventListener("error", () => {
      reject("파일을 읽어오는 데에 실패했습니다.");
    });

    reader.readAsArrayBuffer(file);
  });
};

// Create a new user
export const createUser = async (userDTO) => {
  try {
    let pictureBytes = null;

    if (userDTO.picture) {
      pictureBytes = await convertFileToBytes(userDTO.picture);
    }

    const userPayload = {
      name: userDTO.name,
      phone: userDTO.phone,
      prayerNote: userDTO.prayerNote,
      picture: pictureBytes,
    };

    const response = await axios.post('/users', userPayload);
    return response.data;
  } catch (error) {
    console.error('Error creating user:', error);
    throw error;
  }
};

export const updateUser = async (userDTO) => {
  try {
    const userId = userDTO.id;  // Ensure the userId exists
    if (!userId) {
      throw new Error("User ID is missing");
    }

    // Prepare the JSON payload with byte array (ensure picture is byte[] if needed)
    const userPayload = {
      name: userDTO.name,
      phone: userDTO.phone,
      prayerNote: userDTO.prayerNote,
      picture: userDTO.picture // Include the byte array of the picture if it exists
    };

    // Send the PUT request with `userId` in the URL path and `phone` as a query parameter
    const response = await axios.put(`/users/${userId}`, userPayload);

    return response.data;  // Return the updated user data
  } catch (error) {
    console.error('Error updating user:', error);
    throw error;  // Rethrow the error to be handled by the calling function
  }
};
