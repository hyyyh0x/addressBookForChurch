import React, { useEffect, useState } from 'react';
import { fetchAllUsers, createUser, updateUser } from './api';
import axios from 'axios';
import './UserList.css';

function UserList() {
  const [users, setUsers] = useState([]);
  const [newUser, setNewUser] = useState({ name: '', phone: '', prayerNote: '', picture: null, picturePreview: null });
  const [showUserList, setShowUserList] = useState(true);
  const [showDetails, setShowDetails] = useState(false); // 상세 보기 모드
  const [editMode, setEditMode] = useState(false); // 수정 모드
  const [errorMessage, setErrorMessage] = useState('');
  const [selectedUser, setSelectedUser] = useState(null);
  const [adminPassword, setAdminPassword] = useState('');
  const [currentPage, setCurrentPage] = useState(0);
  const [pageSize] = useState(5);
  const [totalPages, setTotalPages] = useState(1);
  const [searchQuery, setSearchQuery] = useState('');

  const handleSearchChange = (e) => {
    setSearchQuery(e.target.value);
  };

   const handlePreviousPage = () => {
      if (currentPage > 0) {
        setCurrentPage(currentPage - 1);
      }
    };

    const handleNextPage = () => {
      if (currentPage < totalPages - 1) {
        setCurrentPage(currentPage + 1);
      }
    };

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
        resolve(Array.from(byteArray));  // Resolve with byte array
      });

      reader.addEventListener("error", () => {
        reject("파일을 읽어오는 데에 실패했습니다.");
      });

      reader.readAsArrayBuffer(file);
    });
  };

const handleDownloadUser = async (userName, userId) => {
      const enteredPassword = prompt('비밀번호(전화번호)를 입력해주세요.');

      if (enteredPassword === adminPassword) {
        try {
          const response = await axios.get(`/download/${userId}`, {
            responseType: 'arraybuffer',
          });

          const file = new Blob([response.data], { type: 'application/vnd.openxmlformats-officedocument.wordprocessingml.document' });
          const link = document.createElement('a');
          link.href = URL.createObjectURL(file);
          link.download = `User_${userName}.docx`;
          link.click();
          setErrorMessage('');
        } catch (error) {
          console.error('Error downloading user document:', error);
        }
      } else {
         setErrorMessage('잘못된 비밀번호입니다.');
       }
    };

  const handleUpdateUser = async () => {
    try {
      // Convert picture to byte array if a new picture has been selected
      let pictureBytes = null;
      if (newUser.picture instanceof File) {
        pictureBytes = await convertFileToBytes(newUser.picture); // Convert image file to byte array
      }

      // Prepare the updated user object, including the picture as byte array
      const updatedUser = {
        id: newUser.id,
        name: newUser.name,
        phone: newUser.phone,
        prayerNote: newUser.prayerNote,
        picture: pictureBytes || newUser.picture, // Use the new byte array if a picture was selected, otherwise keep the old picture
      };

      // Make the API call to update the user
      await updateUser(updatedUser);

      // Reset the form
      setNewUser({ name: '', phone: '', prayerNote: '', picture: null, picturePreview: null });

      // Reload the list of users and go back to the user list
      loadUsers();
      setShowUserList(true);
      setErrorMessage(''); // Clear error message
    } catch (error) {
      console.error('Error updating user:', error);
      setErrorMessage('업데이트에 실패했습니다. 정보를 확인해주세요.');
    }
  };

  useEffect(() => {
    loadUsers();
    fetchAdminPassword();
  }, [currentPage, searchQuery]);

  const loadUsers = async () => {
    try {
      const usersData = await fetchAllUsers(currentPage, pageSize, searchQuery);
      setUsers(usersData.content);
      setTotalPages(usersData.totalPages);
      setErrorMessage('');
    } catch (error) {
      console.error('Error fetching users:', error);
    }
  };

  const fetchAdminPassword = async () => {
    try {
      const response = await axios.get('/users/admin');
      setAdminPassword(response.data.adminPassword);
    } catch (error) {
      console.error('Error fetching admin password:', error);
    }
  };

  const handleViewDetails = (user) => {
    setSelectedUser(user);
    setShowUserList(false);
    setShowDetails(true);
    setEditMode(false); // 수정 모드는 끔
  };

  const handleEditUser = (user) => {
    const enteredPassword = prompt('비밀번호(전화번호)를 입력해주세요.');
    if (enteredPassword === user.phone || enteredPassword === adminPassword) {
      setNewUser({
        id: user.id,
        name: user.name,
        phone: user.phone,
        prayerNote: user.prayerNote || "",
        picture: user.picture,
        picturePreview: `data:image/jpeg;base64,${user.picture}`,
      });
      setErrorMessage('');
      setShowUserList(false);
      setShowDetails(false);
      setEditMode(true); // 수정 모드로 전환
    } else {
      setErrorMessage('잘못된 비밀번호입니다.');
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setNewUser((prevUser) => ({ ...prevUser, [name]: value }));
  };

  const handleFileChange = (e) => {
    const file = e.target.files[0];
    setNewUser((prevUser) => ({ ...prevUser, picture: file }));
    if (file) {
      const reader = new FileReader();
      reader.onload = () => setNewUser((prevUser) => ({ ...prevUser, picturePreview: reader.result }));
      reader.readAsDataURL(file);
    }
  };

  const handleSaveUser = async () => {
    try {
      await createUser(newUser);
      setNewUser({ name: '', phone: '', prayerNote: '', picture: null, picturePreview: null });
      loadUsers();
      setShowUserList(true);
      setShowDetails(false);
      setEditMode(false);
      setErrorMessage('');
    } catch (error) {
      console.error('Error saving user:', error);
      setErrorMessage('저장에 실패했습니다. 정보를 전부 입력했는지 확인해주세요.');
    }
  };

  return (
  <div>
  <div className="rotate-message">가로 모드로 회전하여 이용해 주세요.</div>
    <div className="user-list-container">
      {errorMessage && <div className="error-message">{errorMessage}</div>}
      {showUserList ? (
        <>
          <h2 className="user-list-header">성도 목록</h2>
          <input
            type="text"
            placeholder="검색할 이름을 입력하세요"
            value={searchQuery}
            onChange={handleSearchChange}
            className="search-input"
          />
          <ul style={{ listStyleType: 'none', padding: 0 }}>
            {users.map((user) => (
              <li key={user.id} className="user-list-item">
                {user.picture && (
                  <img
                    src={`data:image/jpeg;base64,${user.picture}`}
                    alt="User"
                    className="user-image"
                  />
                )}
                <div className="user-info">
                  <p><strong>이름</strong><br/> {user.name}</p>
                  {user.prayerNote && (
                    <p style={{
                      whiteSpace: 'pre-line',
                      maxHeight: '100px',
                      overflow: 'hidden',
                      textOverflow: 'ellipsis',
                      display: '-webkit-box',
                      WebkitLineClamp: 3,
                      WebkitBoxOrient: 'vertical',
                    }}>
                      <strong>기도제목</strong><br/> {user.prayerNote}
                    </p>
                  )}
                </div>
                <div className="user-actions">
                  <button onClick={() => handleViewDetails(user)} className="user-button">자세히 보기</button>
                  <button onClick={() => handleEditUser(user)} className="user-button">수정하기</button>
                  <button onClick={() => handleDownloadUser(user.name, user.id)} className="user-button">다운</button>
                </div>
              </li>
            ))}
          </ul>
          <div className="pagination">
                            <button onClick={handlePreviousPage} disabled={currentPage === 0}>이전</button>
                            <span>{currentPage + 1} / {totalPages}</span>
                            <button onClick={handleNextPage} disabled={currentPage >= totalPages - 1}>다음</button>
                          </div>
        </>
      ) : showDetails ? (
        <div className="user-details">
          <h2>{selectedUser.name}</h2>
          <div>
            {selectedUser.picture && (
              <img
                src={`data:image/jpeg;base64,${selectedUser.picture}`}
                alt="User"
                className="user-image"
              />
            )}
            <p><strong>이름</strong></p>
            <p>{selectedUser.name}</p>
            <p><strong>기도제목</strong></p>
            <p style={{ whiteSpace: 'pre-line' }}>{selectedUser.prayerNote}</p>
            <button onClick={() => setShowUserList(true)}>목록으로 돌아가기</button>
          </div>
        </div>
      ) : editMode ? (
        <div className="user-form">
          <h3>{newUser.id ? "수정" : "추가"}</h3>
          {newUser.picturePreview && (
            <img src={newUser.picturePreview} alt="Preview" />
          )}
          <input
            type="text"
            id="name"
            name="name"
            placeholder="이름"
            value={newUser.name}
            onChange={handleInputChange}
            required
          />
          <input
            type="text"
            id="phone"
            name="phone"
            placeholder="전화번호 (e.g., 010-1234-5678)"
            value={newUser.phone}
            onChange={handleInputChange}
            required
          />
          <textarea
            id="prayerNote"
            name="prayerNote"
            placeholder="기도 제목"
            value={newUser.prayerNote}
            onChange={handleInputChange}
            rows={3}
          />
          <input type="file" id="picture" name="picture" onChange={handleFileChange} />
          {newUser.id ? (
            <button onClick={handleUpdateUser}>업데이트 하기</button>
          ) : (
            <button onClick={handleSaveUser}>저장하기</button>
          )}
          <button onClick={() => { setShowUserList(true); setEditMode(false); }}>성도 목록으로 돌아가기</button>
        </div>
      ) : null}
    </div>
    </div>
  );
}

export default UserList;
